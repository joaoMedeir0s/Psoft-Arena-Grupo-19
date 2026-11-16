package _9.PsoftArena.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import _9.PsoftArena.domain.model.Atividade;
import _9.PsoftArena.domain.model.AvaliacaoFinal;
import _9.PsoftArena.domain.model.AvaliacaoModeloLLM;
import _9.PsoftArena.domain.model.ConfiguracaoLLM;
import _9.PsoftArena.domain.model.Estrategia;
import _9.PsoftArena.domain.model.Estudante;
import _9.PsoftArena.domain.model.Matricula;
import _9.PsoftArena.domain.model.Problema;
import _9.PsoftArena.domain.model.Professor;
import _9.PsoftArena.domain.model.Submissao;
import _9.PsoftArena.domain.model.TipoEstrategia;
import _9.PsoftArena.domain.model.Turma;
import _9.PsoftArena.repository.AtividadeRepository;
import _9.PsoftArena.repository.EstrategiaRepository;
import _9.PsoftArena.repository.EstudanteRepository;
import _9.PsoftArena.repository.ProblemaRepository;
import _9.PsoftArena.repository.ProfessorRepository;
import _9.PsoftArena.repository.SubmissaoRepository;
import _9.PsoftArena.repository.TurmaRepository;
import _9.PsoftArena.dto.request.SubmissaoRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SubmissaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private EstudanteRepository estudanteRepository;

    @Autowired
    private ProblemaRepository problemaRepository;

    @Autowired
    private EstrategiaRepository estrategiaRepository;

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Autowired
    private SubmissaoRepository submissaoRepository;

    private Estudante estudanteMatriculado;
    private Estudante estudanteForaDaTurma;
    private ConfiguracaoLLM llmPadrao;
    private Atividade atividadeAberta;

    @BeforeEach
    void setUp() {
        Professor professor = professorRepository.save(
                new Professor("SIAPE-01", "Prof. Ana", "ana@ufcg.edu.br")
        );

        Turma turmaAtiva = new Turma("PSoft 2026.2", "Projeto de Software", professor);
        estudanteMatriculado = estudanteRepository.save(
                new Estudante("121110001", "João Victor", "joao@ccc.ufcg.edu.br")
        );
        estudanteForaDaTurma = estudanteRepository.save(
                new Estudante("121110002", "Carlos", "carlos@ccc.ufcg.edu.br")
        );

        turmaAtiva.adicionarEstudante(estudanteMatriculado);
        turmaAtiva = turmaRepository.save(turmaAtiva);

        Problema problemaAtivo = problemaRepository.save(
                new Problema(
                        "Sistema Bancário",
                        "Gerenciamento de contas e tarifas",
                        "Permitir saque e depósito",
                        "Aplicar padrão Strategy"
                )
        );

        Estrategia estrategia = estrategiaRepository.save(
                new Estrategia(
                        "Análise GoF",
                        TipoEstrategia.APLICACAO_DE_PADROES_DE_PROJETO,
                        "Avalia aplicação de padrões de projeto",
                        "Avalie o código Java verificando a correta aplicação dos padrões de projeto solicitados."
                )
        );

        llmPadrao = new ConfiguracaoLLM(
                "grok-2-latest",
                "https://api.x.ai/v1/chat/completions",
                "xai-chave-secreta",
                true
        );

        atividadeAberta = atividadeRepository.save(
                new Atividade(
                        "Atividade 1 - Strategy",
                        "Refatorar cálculo de tarifas",
                        LocalDateTime.now().plusDays(5),
                        turmaAtiva,
                        problemaAtivo,
                        estrategia,
                        llmPadrao
                )
        );
    }

    @Test
    void deveSubmeterCodigoEIncrementarVersaoAutomaticamente() throws Exception {
        SubmissaoRequest primeiraSubmissao = new SubmissaoRequest(
                estudanteMatriculado.getMatricula().getCodigo(),
                atividadeAberta.getId(),
                "public class Conta { // v1 }"
        );

        mockMvc.perform(post("/submissoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(primeiraSubmissao)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.versao").value(1));

        SubmissaoRequest segundaSubmissao = new SubmissaoRequest(
                estudanteMatriculado.getMatricula().getCodigo(),
                atividadeAberta.getId(),
                "public class Conta { // v2 corrigida }"
        );

        mockMvc.perform(post("/submissoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segundaSubmissao)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.versao").value(2));
    }

    @Test
    void deveRejeitarSubmissaoDeEstudanteNaoMatriculadoNaTurma() throws Exception {
        SubmissaoRequest request = new SubmissaoRequest(
                estudanteForaDaTurma.getMatricula().getCodigo(),
                atividadeAberta.getId(),
                "public class Conta { }"
        );

        mockMvc.perform(post("/submissoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRejeitarSubmissaoQuandoAtividadeEstiverEncerrada() throws Exception {
        atividadeAberta.encerrar();
        atividadeRepository.save(atividadeAberta);

        SubmissaoRequest request = new SubmissaoRequest(
                estudanteMatriculado.getMatricula().getCodigo(),
                atividadeAberta.getId(),
                "public class Conta { }"
        );

        mockMvc.perform(post("/submissoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveConsultarDetalhesHistoricoEAvaliacaoFinalDaSubmissao() throws Exception {
        Submissao submissao = new Submissao(
                1,
                "public class Conta { }",
                estudanteMatriculado,
                atividadeAberta
        );
        submissao.enfileirar();
        submissao.iniciarProcessamento();

        String evidencias = "Padrão Strategy aplicado na hierarquia Tarifa.";
        String justificativa = "Código bem desacoplado e aderente ao problema.";
        AvaliacaoModeloLLM avaliacaoModelo = new AvaliacaoModeloLLM(9.5, evidencias, justificativa, true, llmPadrao);
        submissao.concluir(new AvaliacaoFinal(9.5, evidencias, justificativa, List.of(avaliacaoModelo)));

        submissao = submissaoRepository.save(submissao);

        mockMvc.perform(get("/submissoes/{id}", submissao.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(submissao.getId()))
                .andExpect(jsonPath("$.versao").value(1))
                .andExpect(jsonPath("$.estudanteMatricula").value(estudanteMatriculado.getMatricula().getCodigo()))
                .andExpect(jsonPath("$.atividadeId").value(atividadeAberta.getId()))
                .andExpect(jsonPath("$.statusAtual").value("CONCLUIDA"))
                .andExpect(jsonPath("$.historicoStatus.length()").value(4));
    }
}