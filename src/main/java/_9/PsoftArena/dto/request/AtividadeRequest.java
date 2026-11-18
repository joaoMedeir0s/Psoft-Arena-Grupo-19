package _9.PsoftArena.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtividadeRequest(
    @NotBlank(message = "O título da atividade é obrigatório.")
    String titulo,

    @NotBlank(message = "A descrição da atividade é obrigatória.")
    String descricao,

    @NotNull(message = "O prazo de submissão é obrigatório.")
    @Future(message = "O prazo de submissão deve ser uma data futura.")
    LocalDateTime prazoSubmissao,

    @NotNull(message = "O código da turma é obrigatório.")
    Long turmaCodigo,

    @NotNull(message = "O ID do problema é obrigatório.")
    Long problemaId,

    @NotNull(message = "O ID da estratégia de avaliação é obrigatório.")
    Long estrategiaId,

    @NotNull(message = "A configuração do LLM padrão é obrigatória.")
    @Valid
    ConfiguracaoLLMRequest llmPadrao
) {}
