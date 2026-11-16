package _9.PsoftArena.domain.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "tb_professor")
public class Professor extends Usuario {

    @EmbeddedId
    private Matricula matriculaDocente;

    @OneToMany(mappedBy = "professor")
    private Set<Turma> turmas = new HashSet<>();

    protected Professor() {
    }

    // Construtor padrao exigido pelo JPA / Hibernate
    public Professor(String codigoMatriculaDocente, String nome, String email) {
        super(nome, email);
        this.matriculaDocente = new Matricula(codigoMatriculaDocente);
    }
}