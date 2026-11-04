package _9.PsoftArena.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@Entity
@Table (name = "tb_estrategia")
public class Estrategia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEstrategia tipo;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(name = "prompt_avaliacao", nullable = false, columnDefinition = "TEXT")
    private String promptAvaliacao;

    protected Estrategia() {
    }
 
    public Estrategia(String nome, TipoEstrategia tipo, String descricao, String promptAvaliacao) {
        this.nome = nome;
        this.tipo = tipo;
        this.descricao = descricao;
        this.promptAvaliacao = promptAvaliacao;
    }
}
