package _9.PsoftArena.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import _9.PsoftArena.domain.model.Atividade;
import _9.PsoftArena.domain.model.AvaliacaoFinal;
import _9.PsoftArena.domain.model.Estudante;
import _9.PsoftArena.domain.model.Matricula;
import _9.PsoftArena.domain.model.NotificadorSubmissao;
import _9.PsoftArena.domain.model.Submissao;
import _9.PsoftArena.dto.request.SubmissaoRequest;
import _9.PsoftArena.dto.response.SubmissaoResponse;
import _9.PsoftArena.repository.AtividadeRepository;
import _9.PsoftArena.repository.EstudanteRepository;
import _9.PsoftArena.repository.SubmissaoRepository;

@Service
public class SubmissaoService {

    private final SubmissaoRepository submissaoRepository;
    private final AtividadeRepository atividadeRepository;
    private final EstudanteRepository estudanteRepository;
    private final List<NotificadorSubmissao> notificadores;
    private final ApplicationEventPublisher eventPublisher;
    private final FilaAvaliacao filaAvaliacao;

    public SubmissaoService(
            SubmissaoRepository submissaoRepository,
            AtividadeRepository atividadeRepository,
            EstudanteRepository estudanteRepository,
            List<NotificadorSubmissao> notificadores,
            ApplicationEventPublisher eventPublisher,
            FilaAvaliacao filaAvaliacao) {
        this.submissaoRepository = submissaoRepository;
        this.atividadeRepository = atividadeRepository;
        this.estudanteRepository = estudanteRepository;
        this.notificadores = notificadores;
        this.eventPublisher = eventPublisher;
        this.filaAvaliacao = filaAvaliacao;
    }

    /**
     * US8 e US9: Cria uma nova submissão com versão incremental (v1, v2...),
     * registra os observadores de status, salva no banco e encaminha para a fila.
     */
    @Transactional
    public SubmissaoResponse criarSubmissao(SubmissaoRequest request) {
        Atividade atividade = buscarAtividadePorId(request.atividadeId());
        Estudante estudante = buscarEstudantePorMatricula(request.matriculaEstudante());

        try {
            // Calcula a próxima versão imutável para (atividade, estudante) (US8)
            int proximaVersao = submissaoRepository.countByAtividadeAndEstudante(atividade, estudante) + 1;

            // Validações de turma, prazo, status da atividade e código ocorrem no domínio
            Submissao novaSubmissao = new Submissao(
                    proximaVersao,
                    request.codigoFonte(),
                    estudante,
                    atividade
            );

            // Anexa os observadores ativos do Spring (ex: NotificadorTerminal) (US9)
            if (notificadores != null) {
                notificadores.forEach(novaSubmissao::adicionarObservador);
            }

            // Transita de SUBMETIDA para EM_FILA registrando no histórico e notificando
            novaSubmissao.enfileirar();

            // Salva inicialmente como SUBMETIDA
            Submissao salva = submissaoRepository.save(novaSubmissao);

            // Persiste a mudança de status e o histórico
            submissaoRepository.save(salva);

            // Publica evento que será recebido após o commit
            eventPublisher.publishEvent(
                new SubmissaoEnfileiradaEvent(salva.getId()));

        return SubmissaoResponse.fromEntity(salva);
                } catch (IllegalArgumentException | IllegalStateException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                }
    }

    /**
     * US9 e US10: Busca os detalhes, histórico de status e avaliação final de uma submissão.
     */
    @Transactional(readOnly = true)
    public SubmissaoResponse buscarPorId(Long id) {
        Submissao submissao = submissaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Submissão não encontrada com ID: " + id));
        return SubmissaoResponse.fromEntity(submissao);
    }

    /**
     * US8 e US9: Lista todas as versões enviadas por um estudante em uma atividade.
     */
    @Transactional(readOnly = true)
    public List<SubmissaoResponse> listarPorAtividadeEEstudante(Long atividadeId, String matriculaEstudante) {
        Atividade atividade = buscarAtividadePorId(atividadeId);
        Estudante estudante = buscarEstudantePorMatricula(matriculaEstudante);

        if (!atividade.getTurma().podeParticiparAtividade(estudante)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Acesso negado: o estudante não está matriculado na turma desta atividade."
            );
        }

        return submissaoRepository.findByAtividadeAndEstudanteOrderByVersaoAsc(atividade, estudante)
                .stream()
                .map(SubmissaoResponse::fromEntity)
                .toList();
    }

    /**
     * US9 e US10 (Visão do Professor): Lista todas as submissões enviadas para uma atividade.
     */
    @Transactional(readOnly = true)
    public List<SubmissaoResponse> listarPorAtividade(Long atividadeId) {
        Atividade atividade = buscarAtividadePorId(atividadeId);

        return submissaoRepository.findByAtividadeOrderByDataHoraSubmissaoDesc(atividade)
                .stream()
                .map(SubmissaoResponse::fromEntity)
                .toList();
    }

    // --- Métodos Auxiliares ---

    private Atividade buscarAtividadePorId(Long id) {
        return atividadeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Atividade não encontrada com ID: " + id));
    }

    private Estudante buscarEstudantePorMatricula(String codigoMatricula) {
        return estudanteRepository.findById(new Matricula(codigoMatricula))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estudante não encontrado com matrícula: " + codigoMatricula));
    }

    @Transactional
    public void iniciarProcessamento(Long id) {

        Submissao submissao = submissaoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Submissão não encontrada."
            ));

        submissao.iniciarProcessamento();

        submissaoRepository.save(submissao);
    }

    @Transactional
    public void registrarFalha(Long id, String motivo) {

        Submissao submissao = submissaoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Submissão não encontrada."
        ));

    submissao.registrarFalha(motivo);

    submissaoRepository.save(submissao);
}

    @Transactional
    public void concluirAvaliacao(
            Long id,
            AvaliacaoFinal avaliacaoFinal) {

        Submissao submissao = submissaoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Submissão não encontrada."
            ));

        submissao.concluir(avaliacaoFinal);

        submissaoRepository.save(submissao);
    }

}
