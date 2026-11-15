package _9.PsoftArena.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_problema")
public class Problema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;
    private String requisitosFuncionais;
    private String requisitosProjeto;

    @Enumerated(EnumType.STRING)
    private StatusProblema status = StatusProblema.ATIVO;

    private boolean utilizadoEmAtividade = false;

    // Construtor padrão exigido pelo JPA
    public Problema() {
    }

    public Problema(String titulo, String descricao, String requisitosFuncionais, String requisitosProjeto) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.requisitosFuncionais = requisitosFuncionais;
        this.requisitosProjeto = requisitosProjeto;
        this.status = StatusProblema.ATIVO;
        this.utilizadoEmAtividade = false;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getRequisitosFuncionais() {
        return requisitosFuncionais;
    }

    public String getRequisitosProjeto() {
        return requisitosProjeto;
    }

    public StatusProblema getStatus() {
        return status;
    }

    public boolean isAtivo() {
        return this.status == StatusProblema.ATIVO;
    }

    public boolean isUtilizadoEmAtividade() {
        return utilizadoEmAtividade;
    }

    public void inativar() {
        this.status = StatusProblema.INATIVO;
    }

    public boolean podeSerRemovido() {
        return !utilizadoEmAtividade;
    }
}