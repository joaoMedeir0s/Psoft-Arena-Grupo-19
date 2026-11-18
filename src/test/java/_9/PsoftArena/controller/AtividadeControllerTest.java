package _9.PsoftArena.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

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
import _9.PsoftArena.domain.model.ConfiguracaoLLM;
import _9.PsoftArena.domain.model.Estrategia;
import _9.PsoftArena.domain.model.Estudante;
import _9.PsoftArena.domain.model.Matricula;
import _9.PsoftArena.domain.model.Problema;
import _9.PsoftArena.domain.model.Professor;
import _9.PsoftArena.domain.model.TipoEstrategia;
import _9.PsoftArena.domain.model.Turma;
import _9.PsoftArena.repository.AtividadeRepository;
import _9.PsoftArena.repository.EstrategiaRepository;
import _9.PsoftArena.repository.EstudanteRepository;
import _9.PsoftArena.repository.ProblemaRepository;
import _9.PsoftArena.repository.ProfessorRepository;
import _9.PsoftArena.repository.TurmaRepository;
import _9.PsoftArena.dto.request.AtividadeRequest;
import _9.PsoftArena.dto.request.ConfiguracaoLLMRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AtividadeControllerTest {

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

    private Professor professor;
    private Turma turmaAtiva;
    private Estudante estudanteMatriculado;
    private Estudante estudanteForaDaTurma;
    private Problema problemaAtivo;
    private Estrategia estrategia;

    @BeforeEach
    void setUp() {
        professor = professorRepository.save(
                new Professor("SIAPE-01", "Prof. Ana", "ana@ufcg.edu.br")
        );

        turmaAtiva = new Turma("PSoft 2026.2", "Projeto de Software", professor);
        estudanteMatriculado = estudanteRepository.save(
                new Estudante("121110001", "João Victor", "joao@ccc.ufcg.edu.br")
        );
        estudanteForaDaTurma = estudanteRepository.save(
                new Estudante("121110002", "Carlos", "carlos@ccc.ufcg.edu.br")
        );

        turmaAtiva.adicionarEstudante(estudanteMatriculado);
        turmaAtiva = turmaRepository.save(turmaAtiva);

        problemaAtivo = problemaRepository.save(
                new Problema(
                        "Sistema Bancário",
                        "Gerenciamento de contas e tarifas",
                        "Permitir saque e depósito",
                        "Aplicar padrão Strategy"
                )
        );

        estrategia = estrategiaRepository.save(
                new Estrategia(
                        "Análise GoF",
                        TipoEstrategia.APLICACAO_DE_PADROES_DE_PROJETO,
                        "Avalia aplicação de padrões de projeto",
                        "Avalie o código Java verificando a correta aplicação dos padrões de projeto solicitados."
                )
        );
    }

    @Test
    void deveCriarAtividadeComSucessoENaoExporChaveApi() throws Exception {
        AtividadeRequest request = new AtividadeRequest(
                "Atividade 1 - Strategy",
                "Refatorar cálculo de tarifas",
                LocalDateTime.now().plusDays(5),
                turmaAtiva.getCodigo(),
                problemaAtivo.getId(),
                estrategia.getId(),
                new ConfiguracaoLLMRequest("grok-2-latest", "https://api.x.ai/v1/chat/completions", "xai-chave-secreta")
        );

        mockMvc.perform(post("/atividades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").value("Atividade 1 - Strategy"))
                .andExpect(jsonPath("$.status").value("ABERTA"))
                .andExpect(jsonPath("$.modelosLLM[0]").value("grok-2-latest"))
                .andExpect(jsonPath("$.chaveApi").doesNotExist());
    }

    @Test
    void deveRetornarBadRequestQuandoTurmaEstiverEncerrada() throws Exception {
        turmaAtiva.encerrar();
        turmaRepository.save(turmaAtiva);

        AtividadeRequest request = new AtividadeRequest(
                "Atividade Inválida",
                "Descrição",
                LocalDateTime.now().plusDays(5),
                turmaAtiva.getCodigo(),
                problemaAtivo.getId(),
                estrategia.getId(),
                new ConfiguracaoLLMRequest("grok-2-latest", "https://api.x.ai/v1/chat/completions", "xai-chave-secreta")
        );

        mockMvc.perform(post("/atividades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveListarAtividadesParaEstudanteMatriculado() throws Exception {
        Atividade atividade = new Atividade(
                "Atividade 1",
                "Descrição",
                LocalDateTime.now().plusDays(5),
                turmaAtiva,
                problemaAtivo,
                estrategia,
                new ConfiguracaoLLM("grok-2-latest", "https://api.x.ai/v1/chat/completions", "xai-chave-secreta", true)
        );
        atividadeRepository.save(atividade);

        mockMvc.perform(get("/atividades/estudante/{matricula}", estudanteMatriculado.getMatricula().getCodigo()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Atividade 1"));
    }

    @Test
    void deveBloquearAcessoQuandoEstudanteNaoPertenceATurma() throws Exception {
        Atividade atividade = atividadeRepository.save(
                new Atividade(
                        "Atividade Restrita",
                        "Descrição",
                        LocalDateTime.now().plusDays(5),
                        turmaAtiva,
                        problemaAtivo,
                        estrategia,
                        new ConfiguracaoLLM("grok-2-latest", "https://api.x.ai/v1/chat/completions", "xai-chave-secreta", true)
                )
        );

        mockMvc.perform(get("/atividades/{id}", atividade.getId())
                        .param("matricula", estudanteForaDaTurma.getMatricula().getCodigo()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveEncerrarAtividadeViaPatch() throws Exception {
        Atividade atividade = atividadeRepository.save(
                new Atividade(
                        "Atividade 1",
                        "Descrição",
                        LocalDateTime.now().plusDays(5),
                        turmaAtiva,
                        problemaAtivo,
                        estrategia,
                        new ConfiguracaoLLM("grok-2-latest", "https://api.x.ai/v1/chat/completions", "xai-chave-secreta", true)
                )
        );

        mockMvc.perform(patch("/atividades/{id}/encerrar", atividade.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));
    }

    @Test
    void deveAdicionarLLMSecundarioViaPost() throws Exception {
        Atividade atividade = atividadeRepository.save(
                new Atividade(
                        "Atividade Multi-LLM",
                        "Descrição",
                        LocalDateTime.now().plusDays(5),
                        turmaAtiva,
                        problemaAtivo,
                        estrategia,
                        new ConfiguracaoLLM("grok-2-latest", "https://api.x.ai/v1/chat/completions", "xai-chave-secreta", true)
                )
        );

        ConfiguracaoLLMRequest secundarioRequest = new ConfiguracaoLLMRequest(
                "gpt-4o",
                "https://api.openai.com/v1/chat/completions",
                "sk-outra-chave"
        );

        mockMvc.perform(post("/atividades/{id}/llms", atividade.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secundarioRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelosLLM.length()").value(2))
                .andExpect(jsonPath("$.modelosLLM[1]").value("gpt-4o"));
    }
}
