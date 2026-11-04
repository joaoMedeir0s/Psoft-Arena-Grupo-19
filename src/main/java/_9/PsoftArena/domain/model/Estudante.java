package _9.PsoftArena.domain.model;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_estudante")
public class Estudante extends Usuario {

    @Id
    private String matricula;

    // Construtor padrão exigido pelo JPA / Hibernate
    public Estudante() {
        super();
    }

    public Estudante(String matricula, String nome, String email) {
        super(nome, email);
        this.matricula = matricula;
    }

    public String getMatricula() {
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