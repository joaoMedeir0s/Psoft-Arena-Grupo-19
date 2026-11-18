package _9.PsoftArena.domain.model;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import _9.PsoftArena.service.ClienteLLM;

@Component
public class ClienteLLMHttp implements ClienteLLM {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ClienteLLMHttp(ObjectMapper objectMapper) {
        this.restClient = RestClient.builder().build();
        this.objectMapper = objectMapper;
    }

    @Override
    public ClienteLLM.ResultadoAvaliacao avaliar(
            ConfiguracaoLLM configuracao,
            String prompt,
            String codigoFonte) {

        String promptCompleto = """
                %s

                Código-fonte submetido:
                ```java
                %s
                ```

                Responda somente com JSON no formato:
                {
                  "nota": 8.5,
                  "evidencias": "evidências encontradas",
                  "justificativa": "justificativa da nota"
                }
                """.formatted(prompt, codigoFonte);

        RespostaAPI resposta = restClient.post()
                .uri(configuracao.getUrlApi())
                .header("Authorization",
                        "Bearer " + configuracao.acessarChaveApiSegura())
                .header("Content-Type", "application/json")
                .body(new Requisicao(
                        configuracao.getNomeModelo(),
                        List.of(
                                new Mensagem("system",
                                        "Você avalia código Java sem executá-lo."),
                                new Mensagem("user", promptCompleto))))
                .retrieve()
                .body(RespostaAPI.class);

        if (resposta == null
                || resposta.choices() == null
                || resposta.choices().isEmpty()) {
            throw new IllegalStateException(
                    "O modelo Grok não retornou uma resposta.");
        }

        try {
            String texto =
                    resposta.choices().getFirst().message().content();

            JsonNode json = objectMapper.readTree(texto);

            double nota = json.path("nota").asDouble(-1);
            String evidencias = json.path("evidencias").asText("");
            String justificativa =
                    json.path("justificativa").asText("");

            if (nota < 0 || nota > 10) {
                throw new IllegalArgumentException(
                        "Nota da LLM fora do intervalo permitido.");
            }

            if (evidencias.isBlank() || justificativa.isBlank()) {
                throw new IllegalArgumentException(
                        "A resposta da LLM está incompleta.");
            }

            return new ClienteLLM.ResultadoAvaliacao(
                    nota,
                    evidencias,
                    justificativa);

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Falha ao interpretar a resposta do Grok.",
                    exception);
        }
    }

    private record Requisicao(
            String model,
            List<Mensagem> messages) {}

    private record Mensagem(String role, String content) {}

    private record RespostaAPI(List<Escolha> choices) {}

    private record Escolha(Mensagem message) {}
}