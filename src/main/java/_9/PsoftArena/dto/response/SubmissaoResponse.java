package _9.PsoftArena.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import _9.PsoftArena.domain.model.AvaliacaoFinal;
import _9.PsoftArena.domain.model.AvaliacaoModeloLLM;
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
    List<HistoricoStatusResponse> historicoStatus,
    AvaliacaoFinalResponse avaliacaoFinal

) {
    public record AvaliacaoFinalResponse(
            double nota,
            String evidencias,
            String justificativa,
            LocalDateTime dataHoraAvaliacao,
            List<AvaliacaoModeloResponse> avaliacoesModelos
    ) {
        public static AvaliacaoFinalResponse fromEntity(AvaliacaoFinal avaliacao) {
            List<AvaliacaoModeloResponse> resultados = avaliacao.getAvaliacoesModelos()
                    .stream()
                    .map(AvaliacaoModeloResponse::fromEntity)
                    .toList();

            return new AvaliacaoFinalResponse(
                    avaliacao.getNota(),
                    avaliacao.getEvidencias(),
                    avaliacao.getJustificativa(),
                    avaliacao.getDataHoraAvaliacao(),
                    resultados);
        }
    }

    public record AvaliacaoModeloResponse(
            String nomeModelo,
            boolean sucesso,
            double nota,
            String evidencias,
            String justificativa
    ) {
        public static AvaliacaoModeloResponse fromEntity(AvaliacaoModeloLLM avaliacao) {
            return new AvaliacaoModeloResponse(
                    avaliacao.getConfiguracaoLLM().getNomeModelo(),
                    avaliacao.getSucesso(),
                    avaliacao.getNota(),
                    avaliacao.getEvidencias(),
                    avaliacao.getJustificativa());
        }
    }

    public static SubmissaoResponse fromEntity(Submissao submissao) {
        List<HistoricoStatusResponse> historico = submissao.getHistoricoStatus()
                .stream()
                .map(HistoricoStatusResponse::fromEntity)
                .toList();
        AvaliacaoFinal avaliacao = submissao.getAvaliacaoFinal();

        return new SubmissaoResponse(
            submissao.getId(),
            submissao.getVersao(),
            submissao.getEstudante().getMatricula().getCodigo(),
            submissao.getEstudante().getNome(),
            submissao.getAtividade().getId(),
            submissao.getAtividade().getTitulo(),
            submissao.getDataHoraSubmissao(),
            submissao.getStatusAtual(),
            historico,
            avaliacao == null ? null : AvaliacaoFinalResponse.fromEntity(avaliacao)
        );
    }
}