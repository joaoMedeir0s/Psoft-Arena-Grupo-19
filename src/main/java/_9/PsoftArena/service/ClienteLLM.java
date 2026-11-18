package _9.PsoftArena.service;

import _9.PsoftArena.domain.model.ConfiguracaoLLM;

public interface ClienteLLM {

    ResultadoAvaliacao avaliar(
            ConfiguracaoLLM configuracao,
            String prompt,
            String codigoFonte
    );

    record ResultadoAvaliacao(
            double nota,
            String evidencias,
            String justificativa
    ) {}
}