package _9.PsoftArena.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmissaoRequest(
    @NotBlank(message = "A matrícula do estudante é obrigatória.")
    String matriculaEstudante,

    @NotNull(message = "O ID da atividade é obrigatório.")
    Long atividadeId,

    @NotBlank(message = "O código-fonte em Java não pode ser vazio.")
    String codigoFonte
) {}
