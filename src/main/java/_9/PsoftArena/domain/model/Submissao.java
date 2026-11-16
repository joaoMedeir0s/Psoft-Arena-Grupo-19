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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;

@Entity
@Table(name = "tb_submissao")
@Getter
public class Submissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int versao;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String codigoFonte;

    @Column(nullable = false)
    private LocalDateTime dataHoraSubmissao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSubmissao statusAtual;

    @Transient
    private List<NotificadorSubmissao> observadores = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "avaliacao_final_id")
    private AvaliacaoFinal avaliacaoFinal;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estudante_matricula", nullable = false)
    private Estudante estudante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "atividade_id", nullable = false)
    private Atividade atividade;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "submissao_id", nullable = false)
    private List<HistoricoStatusSubmissao> historicoStatus = new ArrayList<>();

    protected Submissao() {
    }

    public Submissao(int versao, String codigoFonte, Estudante estudante, Atividade atividade) {
        if (atividade == null || !atividade.podeReceberSubmissoes()) {
            throw new IllegalStateException("A atividade está concluída ou fora do prazo e não aceita novas submissões (US8).");
        }
        if (estudante == null || !atividade.getTurma().podeParticiparAtividade(estudante)) {
            throw new IllegalArgumentException("Apenas estudantes matriculados na turma podem submeter soluções (US8).");
        }
        if (codigoFonte == null || codigoFonte.isBlank()) {
            throw new IllegalArgumentException("O código-fonte em Java não pode ser vazio (US8).");
        }

        this.versao = versao;
        this.codigoFonte = codigoFonte;
        this.estudante = estudante;
        this.atividade = atividade;
        this.dataHoraSubmissao = LocalDateTime.now();
        this.observadores.add(new NotificadorTerminal());

        this.statusAtual = StatusSubmissao.SUBMETIDA;
        this.historicoStatus.add(new HistoricoStatusSubmissao(null, StatusSubmissao.SUBMETIDA, "Submissão recebida."));
        notificarObservadores();
    }

    @PostLoad
    private void inicializarObservadoresPadrao() {
        if (this.observadores == null) {
            this.observadores = new ArrayList<>();
        }
        if (this.observadores.isEmpty()) {
            this.observadores.add(new NotificadorTerminal());
        }
    }

    public void alterarStatus(StatusSubmissao novoStatus, String observacao) {
        StatusSubmissao anterior = this.statusAtual;
        this.statusAtual = novoStatus;
        this.historicoStatus.add(new HistoricoStatusSubmissao(anterior, novoStatus, observacao));
        notificarObservadores();
    }

    public void alterarStatus(StatusSubmissao novoStatus) {
        alterarStatus(novoStatus, "Transição de estado para " + novoStatus);
    }

    public void enfileirar() {
        if (this.statusAtual != StatusSubmissao.SUBMETIDA) {
            throw new IllegalStateException("Apenas submissões no estado SUBMETIDA podem ser enfileiradas.");
        }
        alterarStatus(StatusSubmissao.EM_FILA, "Encaminhada para a fila centralizada de avaliação.");
    }

    public void iniciarProcessamento() {
        if (this.statusAtual != StatusSubmissao.EM_FILA) {
            throw new IllegalStateException("A submissão precisa estar EM_FILA para iniciar o processamento.");
        }
        alterarStatus(StatusSubmissao.PROCESSANDO, "Iniciada análise estática pelos modelos LLM.");
    }

    public void concluir(AvaliacaoFinal avaliacaoFinal) {
        if (this.statusAtual != StatusSubmissao.PROCESSANDO) {
            throw new IllegalStateException("A submissão precisa estar PROCESSANDO para ser concluída.");
        }
        this.avaliacaoFinal = avaliacaoFinal;
        alterarStatus(StatusSubmissao.CONCLUIDA, "Avaliação concluída com sucesso.");
    }

    public void registrarFalha(String motivo) {
        alterarStatus(StatusSubmissao.FALHA, "Falha na avaliação: " + motivo);
    }

    public void adicionarObservador(NotificadorSubmissao observador) {
        if (observador != null && !this.observadores.contains(observador)) {
            this.observadores.add(observador);
        }
    }

    private void notificarObservadores() {
        if (observadores == null) return;
        for (NotificadorSubmissao observador : observadores) {
            observador.notificarMudancaStatus(this);
        }
    }

    public List<HistoricoStatusSubmissao> getHistoricoStatus() {
        return Collections.unmodifiableList(historicoStatus);
    }
}