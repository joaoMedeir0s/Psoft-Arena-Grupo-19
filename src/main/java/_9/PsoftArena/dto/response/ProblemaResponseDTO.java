package _9.PsoftArena.dto.response;

import _9.PsoftArena.domain.model.Problema;

public record ProblemaResponseDTO(
    Long id,
    String titulo, 
    String descricao, 
    String requisitosFuncionais, 
    String requisitosProjeto,
    String status
    //,boolean utilizadoEmAtividade
) {

    public static ProblemaResponseDTO de(Problema problema) {
        return new ProblemaResponseDTO(
            problema.getId(),
            problema.getTitulo(),
            problema.getDescricao(),
            problema.getRequisitosFuncionais(),
            problema.getRequisitosProjeto(),
            problema.getStatus().name()
            //,problema.isUtilizadoEmAtividade()
        );
    }
}
