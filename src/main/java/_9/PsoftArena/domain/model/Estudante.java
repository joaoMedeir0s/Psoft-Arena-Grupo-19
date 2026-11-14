package _9.PsoftArena.domain.model;

import java.util.Objects;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_estudante")
public class Estudante extends Usuario {

    @EmbeddedId 
    private Matricula matricula;

    // Construtor padrão exigido pelo JPA / Hibernate
    public Estudante(String codigoMatricula, String nome, String email) {
        super(nome, email);
        this.matricula = new Matricula(codigoMatricula);
    }

    protected Estudante() {
        // Construtor protegido para uso do JPA
    }

    public Estudante(Matricula matricula, String nome, String email) {
        super(nome, email);
        this.matricula = matricula;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Estudante other)) return false;
        return matricula != null && matricula.equals(other.matricula);
    }
 
    @Override
    public int hashCode() {
        return Objects.hashCode(matricula);
    }
}