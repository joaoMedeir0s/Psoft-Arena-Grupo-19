package _9.PsoftArena.LLMTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;

import _9.PsoftArena.domain.model.ClienteLLMHttp;
import _9.PsoftArena.domain.model.ConfiguracaoLLM;
import _9.PsoftArena.service.ClienteLLM;

/**
 * Testa o ClienteLLMHttp contra um servidor HTTP local simulado.
 * Não usa chave real, internet nem provedor de LLM.
 */
class ClienteLLMHttpTest {

    private static final String CHAVE = "chave-secreta-de-teste";
    private static final String CODIGO = "public class Aluno { }";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private HttpServer servidor;
    private ClienteLLMHttp cliente;
    private ConfiguracaoLLM configuracao;

    // O handler roda em outra thread, por isso volatile
    private volatile int statusResposta = 200;
    private volatile String corpoResposta = "{}";
    private volatile String authorizationRecebido;
    private volatile String corpoRecebido;

    @BeforeEach
    void preparar() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/v1/chat/completions", troca -> {
            authorizationRecebido = troca.getRequestHeaders().getFirst("Authorization");
            corpoRecebido = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            byte[] bytes = corpoResposta.getBytes(StandardCharsets.UTF_8);
            troca.getResponseHeaders().add("Content-Type", "application/json");
            troca.sendResponseHeaders(statusResposta, bytes.length == 0 ? -1 : bytes.length);
            try (OutputStream saida = troca.getResponseBody()) {
                saida.write(bytes);
            }
        });
        servidor.start();

        String url = "http://127.0.0.1:" + servidor.getAddress().getPort() + "/v1/chat/completions";

        cliente = new ClienteLLMHttp(objectMapper);
        configuracao = new ConfiguracaoLLM("modelo-teste", url, CHAVE, true);
    }

    @AfterEach
    void encerrar() {
        servidor.stop(0);
    }

    @Test
    void deveInterpretarRespostaValida() throws Exception {
        responder(200, respostaComConteudo("""
                {"nota": 8.5, "evidencias": "Usa herança corretamente", "justificativa": "Boa modelagem"}"""));

        ClienteLLM.ResultadoAvaliacao resultado =
                cliente.avaliar(configuracao, "Avalie o uso de herança.", CODIGO);

        assertEquals(8.5, resultado.nota(), 0.0001);
        assertEquals("Usa herança corretamente", resultado.evidencias());
        assertEquals("Boa modelagem", resultado.justificativa());
    }

    @Test
    void deveEnviarChaveModeloECodigoNaRequisicao() throws Exception {
        responder(200, respostaComConteudo("""
                {"nota": 7, "evidencias": "x", "justificativa": "y"}"""));

        cliente.avaliar(configuracao, "Avalie.", CODIGO);

        assertEquals("Bearer " + CHAVE, authorizationRecebido);
        assertTrue(corpoRecebido.contains("modelo-teste"));
        assertTrue(corpoRecebido.contains("public class Aluno"));
    }

    @Test
    void deveRejeitarNotaForaDoIntervalo() throws Exception {
        responder(200, respostaComConteudo("""
                {"nota": 11, "evidencias": "x", "justificativa": "y"}"""));

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> cliente.avaliar(configuracao, "Avalie.", CODIGO));

        assertTrue(mensagens(erro).contains("fora do intervalo"));
    }

    @Test
    void deveRejeitarRespostaIncompleta() throws Exception {
        responder(200, respostaComConteudo("""
                {"nota": 5, "evidencias": "x", "justificativa": ""}"""));

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> cliente.avaliar(configuracao, "Avalie.", CODIGO));

        assertTrue(mensagens(erro).contains("incompleta"));
    }

    @Test
    void deveRejeitarConteudoQueNaoEJson() throws Exception {
        responder(200, respostaComConteudo("Desculpe, não consigo avaliar."));

        assertThrows(IllegalStateException.class,
                () -> cliente.avaliar(configuracao, "Avalie.", CODIGO));
    }

    @Test
    void deveRejeitarRespostaSemChoices() {
        responder(200, "{\"choices\": []}");

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> cliente.avaliar(configuracao, "Avalie.", CODIGO));

        assertTrue(mensagens(erro).contains("não retornou"));
    }

    @Test
    void deveFalharQuandoApiRetornaErroHttp() {
        responder(429, "{\"error\": \"limite excedido\"}");

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> cliente.avaliar(configuracao, "Avalie.", CODIGO));

        assertTrue(mensagens(erro).contains("429"));
    }

    private void responder(int status, String corpo) {
        this.statusResposta = status;
        this.corpoResposta = corpo;
    }

    private String respostaComConteudo(String conteudo) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "choices", List.of(Map.of(
                        "message", Map.of("role", "assistant", "content", conteudo)))));
    }

    // Junta a mensagem da exceção e de todas as causas, para não depender de como ela é embrulhada
    private static String mensagens(Throwable erro) {
        StringBuilder texto = new StringBuilder();
        for (Throwable t = erro; t != null; t = t.getCause()) {
            texto.append(t.getMessage()).append(" | ");
        }
        return texto.toString();
    }
}