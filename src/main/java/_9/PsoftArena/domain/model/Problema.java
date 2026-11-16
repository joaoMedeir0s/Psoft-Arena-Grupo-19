package _9.PsoftArena.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "tb_problema")
@Getter
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

   // --- Getters ---
    public boolean isAtivo() {
        return this.status == StatusProblema.ATIVO;
    }

    public boolean isUtilizadoEmAtividade() {
        return utilizadoEmAtividade;
    }

    // --- Setters dos atributos editáveis (US1) ---
    public void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título do problema não pode ser vazio.");
        }
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("A descrição do problema não pode ser vazia.");
        }
        this.descricao = descricao;
    }

    public void setRequisitosFuncionais(String requisitosFuncionais) {
        if (requisitosFuncionais == null || requisitosFuncionais.isBlank()) {
            throw new IllegalArgumentException("Os requisitos funcionais não podem ser vazios.");
        }
        this.requisitosFuncionais = requisitosFuncionais;
    }

    public void setRequisitosProjeto(String requisitosProjeto) {
        if (requisitosProjeto == null || requisitosProjeto.isBlank()) {
            throw new IllegalArgumentException("Os requisitos de projeto não podem ser vazios.");
        }
        this.requisitosProjeto = requisitosProjeto;
    }

    // --- Métodos de Domínio ---
    public void inativar() {
        this.status = StatusProblema.INATIVO;
    }

    public void marcarComoUtilizado() {
        this.utilizadoEmAtividade = true;
    }

    public boolean podeSerRemovido() {
        return !utilizadoEmAtividade;
    }

}