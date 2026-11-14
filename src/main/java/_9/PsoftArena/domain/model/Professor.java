package _9.PsoftArena.domain.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_professor")
public class Professor extends Usuario {
   
    @EmbeddedId
    private Matricula matriculaDocente;
 
    // Construtor padrao exigido pelo JPA / Hibernate
    public Professor(String nome, String email, String codigoMatriculaDocente) {
        super(nome, email);
        this.matriculaDocente = new Matricula(codigoMatriculaDocente);
    }
 
    public Matricula getMatriculaDocente() {
        return matriculaDocente;
    }
}
 