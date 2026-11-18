package _9.PsoftArena.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProcessadorSubmissoes {

    private final FilaAvaliacao filaAvaliacao;
    private final AvaliadorSubmissao avaliadorSubmissao;

    public ProcessadorSubmissoes(
            FilaAvaliacao filaAvaliacao,
            AvaliadorSubmissao avaliadorSubmissao) {

        this.filaAvaliacao = filaAvaliacao;
        this.avaliadorSubmissao = avaliadorSubmissao;
    }

    @Scheduled(fixedDelay = 500)
    public void processarFila() {

        Long submissaoId = filaAvaliacao.poll();

        if (submissaoId != null) {
            avaliadorSubmissao.avaliar(submissaoId);
        }

    }
}