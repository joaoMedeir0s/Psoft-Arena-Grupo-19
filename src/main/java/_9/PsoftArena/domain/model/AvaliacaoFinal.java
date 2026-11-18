package _9.PsoftArena.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "tb_avaliacao_final")
@Getter
public class AvaliacaoFinal extends Avaliacao {

    @Column(nullable = false)
    private LocalDateTime dataHoraAvaliacao;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "avaliacao_final_id")
    private List<AvaliacaoModeloLLM> avaliacoesModelos = new ArrayList<>();

    protected AvaliacaoFinal() {
    }

    public AvaliacaoFinal(double nota, String evidencias, String justificativa,
        List<AvaliacaoModeloLLM> avaliacoesModelos) {
        super(nota, evidencias, justificativa);
        if (avaliacoesModelos == null || avaliacoesModelos.isEmpty()) {
            throw new IllegalArgumentException("A avaliação final deve conter ao menos um resultado de modelo LLM.");
        }
        this.dataHoraAvaliacao = LocalDateTime.now();
        this.avaliacoesModelos = new ArrayList<>(avaliacoesModelos);
    }

    public List<AvaliacaoModeloLLM> getAvaliacoesModelos() {
        return Collections.unmodifiableList(avaliacoesModelos);
    }
}