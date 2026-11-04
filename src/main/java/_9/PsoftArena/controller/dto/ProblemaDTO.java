package _9.PsoftArena.controller.dto;

import _9.PsoftArena.domain.model.Problema;
import _9.PsoftArena.domain.model.StatusProblema;

public class ProblemaDTO {
    private String id;
    private String titulo;
    private String descricao;
    private String requisitosFuncionais;
    private String requisitosProjeto;
    private StatusProblema status;
    private boolean utilizadoEmAtividade;

    public ProblemaDTO() {}

    public ProblemaDTO(Problema problema) {
        this.id = problema.getId();
        this.titulo = problema.getTitulo();
        this.descricao = problema.getDescricao();
        this.requisitosFuncionais = problema.getRequisitosFuncionais();
        this.requisitosProjeto = problema.getRequisitosProjeto();
        this.status = problema.getStatus();
        this.utilizadoEmAtividade = problema.isUtilizadoEmAtividade();
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getRequisitosFuncionais() { return requisitosFuncionais; }
    public void setRequisitosFuncionais(String requisitosFuncionais) { this.requisitosFuncionais = requisitosFuncionais; }
    public String getRequisitosProjeto() { return requisitosProjeto; }
    public void setRequisitosProjeto(String requisitosProjeto) { this.requisitosProjeto = requisitosProjeto; }
    public StatusProblema getStatus() { return status; }
    public void setStatus(StatusProblema status) { this.status = status; }
    public boolean isUtilizadoEmAtividade() { return utilizadoEmAtividade; }
    public void setUtilizadoEmAtividade(boolean utilizadoEmAtividade) { this.utilizadoEmAtividade = utilizadoEmAtividade; }
}