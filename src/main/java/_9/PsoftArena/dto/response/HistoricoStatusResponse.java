package _9.PsoftArena.dto.response;

import java.time.LocalDateTime;

import _9.PsoftArena.domain.model.HistoricoStatusSubmissao;
import _9.PsoftArena.domain.model.StatusSubmissao;

public record HistoricoStatusResponse(
    StatusSubmissao statusAnterior,
    StatusSubmissao statusNovo,
    LocalDateTime dataHoraMudanca,
    String observacao
) {
    public static HistoricoStatusResponse fromEntity(HistoricoStatusSubmissao historico) {
        return new HistoricoStatusResponse(
            historico.getStatusAnterior(),
            historico.getStatusNovo(),
            historico.getDataHoraMudanca(),
            historico.getObservacao()
        );
    }
}
