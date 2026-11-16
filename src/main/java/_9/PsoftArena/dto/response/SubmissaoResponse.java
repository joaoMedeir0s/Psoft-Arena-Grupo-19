package _9.PsoftArena.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import _9.PsoftArena.domain.model.StatusSubmissao;
import _9.PsoftArena.domain.model.Submissao;

public record SubmissaoResponse(
    Long id,
    int versao,
    String estudanteMatricula,
    String estudanteNome,
    Long atividadeId,
    String atividadeTitulo,
    LocalDateTime dataHoraSubmissao,
    StatusSubmissao statusAtual,
    List<HistoricoStatusResponse> historicoStatus

) {
    public static SubmissaoResponse fromEntity(Submissao submissao) {
        List<HistoricoStatusResponse> historico = submissao.getHistoricoStatus()
                .stream()
                .map(HistoricoStatusResponse::fromEntity)
                .toList();

        return new SubmissaoResponse(
            submissao.getId(),
            submissao.getVersao(),
            submissao.getEstudante().getMatricula().getCodigo(),
            submissao.getEstudante().getNome(),
            submissao.getAtividade().getId(),
            submissao.getAtividade().getTitulo(),
            submissao.getDataHoraSubmissao(),
            submissao.getStatusAtual(),
            historico
        );
    }
}