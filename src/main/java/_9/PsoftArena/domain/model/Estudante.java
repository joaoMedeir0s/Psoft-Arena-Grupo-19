package _9.PsoftArena.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_estudante")
public class Estudante {

    @Id
    private String matricula;
    private String nome;
    private String email;

    // Construtor padrão exigido pelo JPA / Hibernate
    public Estudante() {
    }

    public Estudante(String matricula, String nome, String email) {
        this.matricula = matricula;
        this.nome = nome;
        this.email = email;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String novoNome) {
        this.nome = novoNome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String novoEmail) {
        this.email = novoEmail;
    }
}