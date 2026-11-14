package _9.PsoftArena.domain.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable 
public class Matricula implements Serializable {

    @Column(name = "matricula", nullable = false, unique = true, updatable = false)
    private String codigo;

    protected Matricula() {
    }


    public Matricula(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new IllegalArgumentException("O código da matrícula não pode ser nulo ou vazio.");
        }
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override 
    public String toString() {
        return codigo;
    }
}
