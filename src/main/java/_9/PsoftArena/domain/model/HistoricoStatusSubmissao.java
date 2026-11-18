package _9.PsoftArena.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "tb_historico_status_submissao")
@Getter
public class HistoricoStatusSubmissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatusSubmissao statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSubmissao statusNovo;

    @Column(nullable = false)
    private LocalDateTime dataHoraMudanca;

    @Column(length = 500)
    private String observacao;

    protected HistoricoStatusSubmissao() {
    }

    public HistoricoStatusSubmissao(StatusSubmissao statusAnterior, StatusSubmissao statusNovo, String observacao) {
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.dataHoraMudanca = LocalDateTime.now();
        this.observacao = observacao;
    }
}