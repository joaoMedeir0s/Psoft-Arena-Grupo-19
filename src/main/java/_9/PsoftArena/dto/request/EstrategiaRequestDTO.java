package _9.PsoftArena.dto.request;

import _9.PsoftArena.domain.model.TipoEstrategia;

public record EstrategiaRequestDTO(
        String nome,
        String descricao,
        TipoEstrategia tipoEstrategia,
        String promptAvaliacao
) {

}
