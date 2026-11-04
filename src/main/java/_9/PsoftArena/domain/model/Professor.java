package _9.PsoftArena.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

@Entity
@Table(name = "tb_professor")
public class Professor extends Usuario {
 
    @Id
    private String matriculaDocente;
 
    // Construtor padrao exigido pelo JPA / Hibernate
    public Professor() {
        super();
    }
 
    public Professor(String matriculaDocente, String nome, String email) {
        super(nome, email);
        this.matriculaDocente = matriculaDocente;
    }
 
    public String getMatriculaDocente() {
        return matriculaDocente;
    }
}
 