package _9.PsoftArena.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AtividadeTest {

    private Professor professor;
    private Turma turmaAtiva;
    private Problema problemaAtivo;
    private Estrategia estrategia;
    private ConfiguracaoLLM llmPadrao;
    private LocalDateTime prazoValido;

    @BeforeEach
    void setUp() {
        professor = new Professor("SIAPE-01", "Prof. Ana", "ana@ufcg.edu.br");
        turmaAtiva = new Turma("PSoft 2026.2", "Projeto de Software", professor);
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
        prazoValido = LocalDateTime.now().plusDays(7);
    }

    @Test
    void deveCriarAtividadeComSucessoEMarcarProblemaComoUtilizado() {
        Atividade atividade = new Atividade(
                "Atividade 1 - Strategy",
                "Refatorar cálculo de tarifas",
                prazoValido,
                turmaAtiva,
                problemaAtivo,
                estrategia,
                llmPadrao
        );

        assertEquals("Atividade 1 - Strategy", atividade.getTitulo());
        assertEquals(StatusAtividade.ABERTA, atividade.getStatus());
        assertEquals(1, atividade.getConfiguracoesLLM().size());
        assertTrue(atividade.podeReceberSubmissoes());
        // Regra US1 / US6: problema passa a ser marcado como utilizado em atividade
        assertTrue(problemaAtivo.isUtilizadoEmAtividade());
    }

    @Test
    void deveBloquearCriacaoQuandoTurmaEstiverEncerrada() {
        turmaAtiva.encerrar();

        assertThrows(IllegalArgumentException.class, () ->
            new Atividade(
                "Atividade Inválida",
                "Descrição",
                prazoValido,
                turmaAtiva,
                problemaAtivo,
                estrategia,
                llmPadrao
            )
        );
    }

    @Test
    void deveBloquearCriacaoQuandoProblemaEstiverInativo() {
        problemaAtivo.inativar();

        assertThrows(IllegalArgumentException.class, () ->
            new Atividade(
                "Atividade Inválida",
                "Descrição",
                prazoValido,
                turmaAtiva,
                problemaAtivo,
                estrategia,
                llmPadrao
            )
        );
    }

    @Test
    void deveRecusarSubmissoesQuandoCriadaComPrazoNoPassado() {
        LocalDateTime prazoExpirado = LocalDateTime.now().minusMinutes(1);

        Atividade atividade = new Atividade(
                "Atividade Expirada",
                "Descrição",
                prazoExpirado,
                turmaAtiva,
                problemaAtivo,
                estrategia,
                llmPadrao
        );

        assertFalse(atividade.podeReceberSubmissoes());
    }

    @Test
    void deveEncerrarAtividadeEBloquearNovasSubmissoes() {
        Atividade atividade = new Atividade(
                "Atividade 1",
                "Descrição",
                prazoValido,
                turmaAtiva,
                problemaAtivo,
                estrategia,
                llmPadrao
        );

        atividade.encerrar();

        assertEquals(StatusAtividade.CONCLUIDA, atividade.getStatus());
        assertFalse(atividade.podeReceberSubmissoes());
    }

    @Test
    void deveRecusarSubmissoesAposPrazoMesmoComStatusAberta() throws InterruptedException {
        LocalDateTime prazoCurto = LocalDateTime.now().plus(100, ChronoUnit.MILLIS);

        Atividade atividade = new Atividade(
                "Atividade 1",
                "Descrição",
                prazoCurto,
                turmaAtiva,
                problemaAtivo,
                estrategia,
                llmPadrao
        );

        Thread.sleep(150);

        assertEquals(StatusAtividade.ABERTA, atividade.getStatus());
        assertFalse(atividade.podeReceberSubmissoes());
    }

    @Test
    void deveAdicionarLLMSecundarioParaAvaliacaoMultiModelo() {
        Atividade atividade = new Atividade(
                "Atividade Multi-LLM",
                "Descrição",
                prazoValido,
                turmaAtiva,
                problemaAtivo,
                estrategia,
                llmPadrao
        );

        ConfiguracaoLLM llmSecundario = new ConfiguracaoLLM(
                "gpt-4o",
                "https://api.openai.com/v1/chat/completions",
                "sk-outra-chave",
                false
        );

        atividade.adicionarLLMSecundario(llmSecundario);

        assertEquals(2, atividade.getConfiguracoesLLM().size());
        assertFalse(llmSecundario.getPadrao());
    }
}