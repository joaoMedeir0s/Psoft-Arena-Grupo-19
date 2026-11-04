package _9.PsoftArena.domain.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity 
@Table(name = "tb_turma")
@Getter 
public class Turma {
   
    @Column(nullable = false)
    private String nome;

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String periodoLetivo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTurma statusTurma;

    @ManyToMany 
    @JoinTable(
            name = "turma_estudante",
            joinColumns = @JoinColumn(name = "turma_id"),
            inverseJoinColumns = @JoinColumn(name = "estudante_id"))
    private List<Estudante> estudantes = new ArrayList<>();

     @Column(nullable = false, unique = true)
    private String codigo;

    public Turma(String codigo, String nome, String periodoLetivo) {
        this.codigo = codigo;
        this.nome = nome;
        this.periodoLetivo = periodoLetivo;
        this.statusTurma = StatusTurma.ATIVA; 
    }

    public void encerrar() {
        this.statusTurma = StatusTurma.ENCERRADA;
    }

    public boolean podeReceberMatriculas(Estudante estudante) {
        return statusTurma == StatusTurma.ATIVA && !estudantes.contains(estudante);
    }

    public boolean podeReceberAtividades() {
        return statusTurma == StatusTurma.ATIVA;
    }

    public void adicionarEstudante(Estudante estudante) {
        if (!podeReceberMatriculas(estudante)) {
            throw new IllegalStateException("Turma já encerrada");
        }
        estudantes.add(estudante);
    }
}
