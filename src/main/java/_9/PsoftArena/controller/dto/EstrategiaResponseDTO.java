package _9.PsoftArena.controller.dto;

import _9.PsoftArena.domain.model.Estrategia;
import _9.PsoftArena.domain.model.TipoEstrategia;

public record EstrategiaResponseDTO(
        Long id,
        String nome,
        TipoEstrategia tipo,
        String descricao,
        String promptAvaliacao
) {
    public static EstrategiaResponseDTO de(Estrategia e) {
        return new EstrategiaResponseDTO(
                e.getId(), e.getNome(), e.getTipo(), e.getDescricao(), e.getPromptAvaliacao());
    }
}
