package _9.PsoftArena.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "tb_avaliacao_modelo_llm")
@Getter
public class AvaliacaoModeloLLM extends Avaliacao {

    @Column(nullable = false)
    private boolean sucesso;

    @ManyToOne(optional = false)
    @JoinColumn(name = "configuracao_llm_id", nullable = false)
    private ConfiguracaoLLM configuracaoLLM;

    protected AvaliacaoModeloLLM() {
    }

    public AvaliacaoModeloLLM(double nota, String evidencias, String justificativa,
        boolean sucesso, ConfiguracaoLLM configuracaoLLM) {
        super(nota, evidencias, justificativa);
        this.sucesso = sucesso;
        this.configuracaoLLM = configuracaoLLM;
    }

    public boolean getSucesso() {
        return sucesso;
    }
}