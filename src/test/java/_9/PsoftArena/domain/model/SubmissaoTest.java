package _9.PsoftArena.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubmissaoTest {

    private Professor professor;
    private Turma turmaAtiva;
    private Estudante estudanteMatriculado;
    private Estudante estudanteNaoMatriculado;
    private Problema problemaAtivo;
    private Estrategia estrategia;
    private ConfiguracaoLLM llmPadrao;
    private Atividade atividadeAberta;

    @BeforeEach
    void setUp() {
        professor = new Professor("SIAPE-01", "Prof. Ana", "ana@ufcg.edu.br");
        turmaAtiva = new Turma("PSoft 2026.2", "Projeto de Software", professor);

        estudanteMatriculado = new Estudante("121110001", "João Victor", "joao@ccc.ufcg.edu.br");
        estudanteNaoMatriculado = new Estudante("121110002", "Carlos", "carlos@ccc.ufcg.edu.br");
        turmaAtiva.adicionarEstudante(estudanteMatriculado);

        problemaAtivo = new Problema(
                "Sistema Bancário",
                "Gerenciamento de contas e tarifas",
                "Permitir saque e depósito",
                "Aplicar padrão Strategy"
        );

        estrategia = new Estrategia(
                "Análise GoF",
                TipoEstrategia.APLICACAO_DE_PADROES_DE_PROJETO,
                "Avalia aplicação de padrões de projeto",
                "Avalie o código Java verificando a correta aplicação dos padrões de projeto solicitados."
        );

        llmPadrao = new ConfiguracaoLLM(
                "grok-2-latest",
                "https://api.x.ai/v1/chat/completions",
                "xai-chave-secreta",
                true
        );

        atividadeAberta = new Atividade(
                "Atividade 1 - Strategy",
                "Refatorar cálculo de tarifas",
                LocalDateTime.now().plusDays(5),
                turmaAtiva,
                problemaAtivo,
                estrategia,
                llmPadrao
        );
    }

    @Test
    void deveCriarSubmissaoEmEstadoSubmetidaERegistrarHistoricoInicial() {
        Submissao submissao = new Submissao(
                1,
                "public class Conta { }",
                estudanteMatriculado,
                atividadeAberta
        );

        assertEquals(1, submissao.getVersao());
        assertEquals(StatusSubmissao.SUBMETIDA, submissao.getStatusAtual());
        assertEquals(1, submissao.getHistoricoStatus().size());
        assertEquals(StatusSubmissao.SUBMETIDA, submissao.getHistoricoStatus().get(0).getStatusNovo());
    }

    @Test
    void deveBloquearSubmissaoDeEstudanteNaoMatriculadoNaTurma() {
        assertThrows(IllegalArgumentException.class, () ->
            new Submissao(
                1,
                "public class Conta { }",
                estudanteNaoMatriculado,
                atividadeAberta
            )
        );
    }

    @Test
    void deveBloquearSubmissaoQuandoAtividadeEstiverEncerrada() {
        atividadeAberta.encerrar();

        assertThrows(IllegalStateException.class, () ->
            new Submissao(
                1,
                "public class Conta { }",
                estudanteMatriculado,
                atividadeAberta
            )
        );
    }

    @Test
    void deveBloquearSubmissaoQuandoPrazoEstiverExpirado() throws InterruptedException {
        Atividade atividadePrazoCurto = new Atividade(
                "Atividade Curta",
                "Descrição",
                LocalDateTime.now().plus(100, ChronoUnit.MILLIS),
                turmaAtiva,
                problemaAtivo,
                estrategia,
                llmPadrao
        );

        Thread.sleep(150);

        assertThrows(IllegalStateException.class, () ->
            new Submissao(
                1,
                "public class Conta { }",
                estudanteMatriculado,
                atividadePrazoCurto
            )
        );
    }

    @Test
    void deveBloquearSubmissaoComCodigoVazioOuNulo() {
        assertThrows(IllegalArgumentException.class, () ->
            new Submissao(1, "", estudanteMatriculado, atividadeAberta)
        );

        assertThrows(IllegalArgumentException.class, () ->
            new Submissao(1, null, estudanteMatriculado, atividadeAberta)
        );
    }

    @Test
    void devePercorrerFluxoCompletoDeProcessamentoComSucesso() {
        Submissao submissao = new Submissao(
                1,
                "public class Conta { }",
                estudanteMatriculado,
                atividadeAberta
        );

        submissao.enfileirar();
        assertEquals(StatusSubmissao.EM_FILA, submissao.getStatusAtual());

        submissao.iniciarProcessamento();
        assertEquals(StatusSubmissao.PROCESSANDO, submissao.getStatusAtual());

        AvaliacaoFinal avaliacao = criarAvaliacaoFinalValida(9.5, "Excelente aplicação do padrão Strategy.");
        submissao.concluir(avaliacao);

        assertEquals(StatusSubmissao.CONCLUIDA, submissao.getStatusAtual());
        assertNotNull(submissao.getAvaliacaoFinal());
        assertEquals(9.5, submissao.getAvaliacaoFinal().getNota());
        assertEquals("Excelente aplicação do padrão Strategy.", submissao.getAvaliacaoFinal().getJustificativa());
        assertEquals(4, submissao.getHistoricoStatus().size());
    }

    @Test
    void deveRegistrarFalhaDuranteProcessamento() {
        Submissao submissao = new Submissao(
                1,
                "public class Conta { }",
                estudanteMatriculado,
                atividadeAberta
        );

        submissao.enfileirar();
        submissao.iniciarProcessamento();
        submissao.registrarFalha("Timeout ao comunicar com API da LLM.");

        assertEquals(StatusSubmissao.FALHA, submissao.getStatusAtual());
        assertEquals(4, submissao.getHistoricoStatus().size());
    }

    @Test
    void deveImpedirTransicoesInvalidasDeEstado() {
        Submissao submissao = new Submissao(
                1,
                "public class Conta { }",
                estudanteMatriculado,
                atividadeAberta
        );

        // Não pode iniciar processamento sem antes passar por EM_FILA
        assertThrows(IllegalStateException.class, submissao::iniciarProcessamento);

        // Não pode concluir direto de SUBMETIDA
        AvaliacaoFinal avaliacao = criarAvaliacaoFinalValida(10.0, "Feedback");
        assertThrows(IllegalStateException.class, () ->
            submissao.concluir(avaliacao)
        );
    }

    @Test
    void deveNotificarObservadoresACadaMudancaDeStatus() {
        Submissao submissao = new Submissao(
                1,
                "public class Conta { }",
                estudanteMatriculado,
                atividadeAberta
        );

        List<StatusSubmissao> statusNotificados = new ArrayList<>();
        submissao.adicionarObservador(sub ->
            statusNotificados.add(sub.getStatusAtual())
        );

        submissao.enfileirar();
        submissao.iniciarProcessamento();
        submissao.concluir(criarAvaliacaoFinalValida(8.0, "Bom trabalho."));

        assertEquals(
                List.of(StatusSubmissao.EM_FILA, StatusSubmissao.PROCESSANDO, StatusSubmissao.CONCLUIDA),
                statusNotificados
        );
    }

    private AvaliacaoFinal criarAvaliacaoFinalValida(double nota, String justificativa) {
        String evidencias = "Classes EstrategiaTarifa e Conta implementadas corretamente.";
        AvaliacaoModeloLLM resultadoModelo = new AvaliacaoModeloLLM(
                nota,
                evidencias,
                justificativa,
                true,
                llmPadrao
        );
        return new AvaliacaoFinal(
                nota,
                evidencias,
                justificativa,
                List.of(resultadoModelo)
        );
    }

}