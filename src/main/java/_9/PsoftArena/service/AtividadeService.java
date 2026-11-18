package _9.PsoftArena.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import _9.PsoftArena.domain.model.Atividade;
import _9.PsoftArena.domain.model.ConfiguracaoLLM;
import _9.PsoftArena.domain.model.Estrategia;
import _9.PsoftArena.domain.model.Estudante;
import _9.PsoftArena.domain.model.Matricula;
import _9.PsoftArena.domain.model.Problema;
import _9.PsoftArena.domain.model.Turma;
import _9.PsoftArena.repository.AtividadeRepository;
import _9.PsoftArena.repository.EstrategiaRepository;
import _9.PsoftArena.repository.EstudanteRepository;
import _9.PsoftArena.repository.ProblemaRepository;
import _9.PsoftArena.repository.TurmaRepository;
import _9.PsoftArena.dto.response.AtividadeResponse;
import _9.PsoftArena.dto.request.ConfiguracaoLLMRequest;
import _9.PsoftArena.dto.request.AtividadeRequest;

@Service
public class AtividadeService {

    private final AtividadeRepository atividadeRepository;
    private final TurmaRepository turmaRepository;
    private final ProblemaRepository problemaRepository;
    private final EstrategiaRepository estrategiaRepository;
    private final EstudanteRepository estudanteRepository;

    public AtividadeService(
            AtividadeRepository atividadeRepository,
            TurmaRepository turmaRepository,
            ProblemaRepository problemaRepository,
            EstrategiaRepository estrategiaRepository,
            EstudanteRepository estudanteRepository) {
        this.atividadeRepository = atividadeRepository;
        this.turmaRepository = turmaRepository;
        this.problemaRepository = problemaRepository;
        this.estrategiaRepository = estrategiaRepository;
        this.estudanteRepository = estudanteRepository;
    }

    /**
     * US6: Cria uma atividade associada a uma turma ativa, problema ativo,
     * estratégia única e LLM padrão.
     */
    @Transactional
    public AtividadeResponse criarAtividade(AtividadeRequest request) {
        Turma turma = turmaRepository.findById(request.turmaCodigo())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Turma não encontrada com código: " + request.turmaCodigo()));

        Problema problema = problemaRepository.findById(request.problemaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Problema não encontrado com ID: " + request.problemaId()));

        Estrategia estrategia = estrategiaRepository.findById(request.estrategiaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estratégia não encontrada com ID: " + request.estrategiaId()));

        try {
            ConfiguracaoLLM llmPadrao = new ConfiguracaoLLM(
                    request.llmPadrao().nomeModelo(),
                    request.llmPadrao().urlApi(),
                    request.llmPadrao().chaveApi(),
                    true // Marcado como padrão na criação (US6)
            );

            // As validações de turma ativa, problema ativo e marcação de uso
            // ocorrem dentro do construtor rico de Atividade
            Atividade novaAtividade = new Atividade(
                    request.titulo(),
                    request.descricao(),
                    request.prazoSubmissao(),
                    turma,
                    problema,
                    estrategia,
                    llmPadrao
            );

            problemaRepository.save(problema); // Persiste utilizadoEmAtividade = true (US1)
            Atividade salva = atividadeRepository.save(novaAtividade);

            return AtividadeResponse.fromEntity(salva);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * US6: Encerra a atividade, alterando o status para CONCLUIDA.
     */
    @Transactional
    public AtividadeResponse encerrarAtividade(Long id) {
        Atividade atividade = buscarAtividadePorId(id);
        atividade.encerrar();
        Atividade salva = atividadeRepository.save(atividade);
        return AtividadeResponse.fromEntity(salva);
    }

    /**
     * US10: Adiciona um modelo de LLM secundário para avaliação multi-modelo.
     */
    @Transactional
    public AtividadeResponse adicionarLLMSecundario(Long id, ConfiguracaoLLMRequest request) {
        Atividade atividade = buscarAtividadePorId(id);

        try {
            ConfiguracaoLLM llmSecundario = new ConfiguracaoLLM(
                    request.nomeModelo(),
                    request.urlApi(),
                    request.chaveApi(),
                    false // Secundário
            );

            atividade.adicionarLLMSecundario(llmSecundario);
            Atividade salva = atividadeRepository.save(atividade);
            return AtividadeResponse.fromEntity(salva);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * US7: Lista apenas as atividades das turmas das quais o estudante participa.
     */
    @Transactional(readOnly = true)
    public List<AtividadeResponse> listarAtividadesPorEstudante(String codigoMatricula) {
        Estudante estudante = buscarEstudantePorMatricula(codigoMatricula);

        return atividadeRepository.findByTurmaEstudantesContaining(estudante)
                .stream()
                .map(AtividadeResponse::fromEntity)
                .toList();
    }

    /**
     * US7: Retorna os detalhes de uma atividade específica se o estudante
     * fizer parte da turma da atividade; bloqueia caso contrário.
     */
    @Transactional(readOnly = true)
    public AtividadeResponse buscarAtividadeParaEstudante(Long atividadeId, String codigoMatricula) {
        Atividade atividade = buscarAtividadePorId(atividadeId);
        Estudante estudante = buscarEstudantePorMatricula(codigoMatricula);

        if (!atividade.getTurma().podeParticiparAtividade(estudante)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Acesso negado: o estudante não está matriculado na turma desta atividade (US7)."
            );
        }

        return AtividadeResponse.fromEntity(atividade);
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
}