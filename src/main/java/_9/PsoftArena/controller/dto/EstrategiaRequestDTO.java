package _9.PsoftArena.controller.dto;

import _9.PsoftArena.domain.model.TipoEstrategia;

public record EstrategiaRequestDTO(
        String nome,
        String descricao,
        TipoEstrategia tipoEstrategia,
        String promptAvaliacao
) {

}
