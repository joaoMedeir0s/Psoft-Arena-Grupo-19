package _9.PsoftArena.domain.model;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter  
@MappedSuperclass 
public abstract class Usuario {
    private String nome;
    private String email;

    protected Usuario() {
    }

    public Usuario(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }
}
