package _9.PsoftArena.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import _9.PsoftArena.domain.model.Atividade;
import _9.PsoftArena.domain.model.ConfiguracaoLLM;
import _9.PsoftArena.domain.model.StatusAtividade;

public record AtividadeResponse(
    Long id,
    String titulo,
    String descricao,
    LocalDateTime prazoSubmissao,
    StatusAtividade status,
    Long turmaCodigo,
    String turmaNome,
    Long problemaId,
    String problemaTitulo,
    String requisitosFuncionais,
    String requisitosProjeto,
    String estrategiaNome,
    String estrategiaTipo,
    List<String> modelosLLM
) {
    public static AtividadeResponse fromEntity(Atividade atividade) {
        List<String> nomesModelos = atividade.getConfiguracoesLLM()
                .stream()
                .map(ConfiguracaoLLM::getNomeModelo)
                .toList();

        return new AtividadeResponse(
            atividade.getId(),
            atividade.getTitulo(),
            atividade.getDescricao(),
            atividade.getPrazoSubmissao(),
            atividade.getStatus(),
            atividade.getTurma().getCodigo(),
            atividade.getTurma().getNome(),
            atividade.getProblema().getId(),
            atividade.getProblema().getTitulo(),
            atividade.getProblema().getRequisitosFuncionais(),
            atividade.getProblema().getRequisitosProjeto(),
            atividade.getEstrategia().getNome(),
            atividade.getEstrategia().getTipoEstrategia().name(),
            nomesModelos
        );
    }
}
