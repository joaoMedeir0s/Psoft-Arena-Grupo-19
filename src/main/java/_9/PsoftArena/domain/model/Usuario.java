package _9.PsoftArena.domain.model;

import jakarta.persistence.Column;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass 
public abstract class Usuario {

    @Column(nullable = false)
    private String nome;

    @Column (nullable = false, unique = true)
    private String email;

    protected Usuario() {
        // Construtor protegido para uso do JPA
    }

    protected Usuario(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
