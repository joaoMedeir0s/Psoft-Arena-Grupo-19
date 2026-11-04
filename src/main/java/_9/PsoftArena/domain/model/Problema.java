package _9.PsoftArena.domain.model;

public class Problema {
    private String id;
    private String titulo;
    private String descricao;
    private String requisitosFuncionais;
    private String requisitosProjeto;
    private StatusProblema status;
    private boolean utilizadoEmAtividade;

    public Problema(String id, String titulo, String descricao, String requisitosFuncionais, String requisitosProjeto) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.requisitosFuncionais = requisitosFuncionais;
        this.requisitosProjeto = requisitosProjeto;
        this.status = StatusProblema.ATIVO; // Regra US1: nasce ATIVO
        this.utilizadoEmAtividade = false;
    }

    public void inativar() {
        this.status = StatusProblema.INATIVO;
    }

    public void ativar() {
        this.status = StatusProblema.ATIVO;
    }

    public boolean podeSerRemovido() {
        return !this.utilizadoEmAtividade; // Regra US1: não pode ser removido se já usado
    }

    // Getters e Setters
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getRequisitosFuncionais() { return requisitosFuncionais; }
    public void setRequisitosFuncionais(String requisitosFuncionais) { this.requisitosFuncionais = requisitosFuncionais; }
    public String getRequisitosProjeto() { return requisitosProjeto; }
    public void setRequisitosProjeto(String requisitosProjeto) { this.requisitosProjeto = requisitosProjeto; }
    public StatusProblema getStatus() { return status; }
    public boolean isUtilizadoEmAtividade() { return utilizadoEmAtividade; }
    public void setUtilizadoEmAtividade(boolean utilizadoEmAtividade) { this.utilizadoEmAtividade = utilizadoEmAtividade; }
}