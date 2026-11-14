package _9.PsoftArena.dto.request;


public record ProblemaRequestDTO(
    String titulo, 
    String descricao, 
    String requisitosFuncionais, 
    String requisitosProjeto
// ,boolean utilizadoEmAtividade
){}
