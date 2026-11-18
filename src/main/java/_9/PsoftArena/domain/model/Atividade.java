package _9.PsoftArena.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "tb_atividade")
@Getter
public class Atividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime prazoSubmissao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAtividade status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "turma_codigo", nullable = false)
    private Turma turma;

    @ManyToOne(optional = false)
    @JoinColumn(name = "problema_id", nullable = false)
    private Problema problema;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estrategia_id", nullable = false)
    private Estrategia estrategia;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
        name = "atividade_configuracao_llm",
        joinColumns = @JoinColumn(name = "atividade_id"),
        inverseJoinColumns = @JoinColumn(name = "configuracao_llm_id")
    )
    private List<ConfiguracaoLLM> configuracoesLLM = new ArrayList<>();

    protected Atividade() {
    }

    public Atividade(String titulo, String descricao, LocalDateTime prazoSubmissao,
        Turma turma, Problema problema, Estrategia estrategia, ConfiguracaoLLM llmPadrao) {
        if (turma == null || !turma.podeReceberAtividades()) {
            throw new IllegalArgumentException("A turma deve estar ativa para receber atividades (US6).");
        }
        if (problema == null || !problema.isAtivo()) {
            throw new IllegalArgumentException("O problema deve estar ativo para ser associado a uma atividade (US6).");
        }
        if (estrategia == null) {
            throw new IllegalArgumentException("A atividade deve possuir uma estratégia de avaliação (US6).");
        }
        if (llmPadrao == null || !llmPadrao.getPadrao()) {
            throw new IllegalArgumentException("A atividade deve ser criada com um LLM padrão válido (US6).");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título da atividade é obrigatório.");
        }
        if (prazoSubmissao == null) {
            throw new IllegalArgumentException("Prazo de submissão é obrigatório.");
        }

        this.titulo = titulo;
        this.descricao = descricao;
        this.prazoSubmissao = prazoSubmissao;
        this.status = StatusAtividade.ABERTA;
        this.turma = turma;
        this.problema = problema;
        this.estrategia = estrategia;
        this.configuracoesLLM.add(llmPadrao);

        // Impede exclusão futura do problema (US1)
        this.problema.marcarComoUtilizado();
    }

    public boolean isAberta() {
        return this.status == StatusAtividade.ABERTA;
    }

    public void encerrar() {
        this.status = StatusAtividade.CONCLUIDA;
    }

    public boolean podeReceberSubmissoes() {
        return isAberta() && LocalDateTime.now().isBefore(prazoSubmissao);
    }

    public void adicionarLLMSecundario(ConfiguracaoLLM llmSecundario) {
        if (llmSecundario == null) {
            throw new IllegalArgumentException("Configuração de LLM secundário inválida.");
        }
        this.configuracoesLLM.add(llmSecundario);
    }

    public List<ConfiguracaoLLM> getConfiguracoesLLM() {
        return Collections.unmodifiableList(configuracoesLLM);
    }
}