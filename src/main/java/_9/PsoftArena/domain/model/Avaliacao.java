package _9.PsoftArena.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "tb_avaliacao")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
public abstract class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double nota;

    @Column(columnDefinition = "TEXT")
    private String evidencias;

    @Column(columnDefinition = "TEXT")
    private String justificativa;

    protected Avaliacao() {
    }

    protected Avaliacao(double nota, String evidencias, String justificativa) {
        if (nota < 0.0 || nota > 10.0) {
            throw new IllegalArgumentException("A nota da avaliação deve estar entre 0 e 10 (US10).");
        }
        this.nota = nota;
        this.evidencias = evidencias;
        this.justificativa = justificativa;
    }
}