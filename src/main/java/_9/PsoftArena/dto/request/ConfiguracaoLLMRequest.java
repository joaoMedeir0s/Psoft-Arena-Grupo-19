package _9.PsoftArena.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ConfiguracaoLLMRequest(
    @NotBlank(message = "O nome do modelo LLM é obrigatório.")
    String nomeModelo,

    @NotBlank(message = "A URL da API é obrigatória.")
    String urlApi,

    @NotBlank(message = "A chave de acesso à API é obrigatória.")
    String chaveApi
) {}
