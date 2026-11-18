package _9.PsoftArena.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProcessadorSubmissoes {

    private final FilaAvaliacao filaAvaliacao;
    private final SubmissaoService submissaoService;

    public ProcessadorSubmissoes(
            FilaAvaliacao filaAvaliacao,
            SubmissaoService submissaoService) {

        this.filaAvaliacao = filaAvaliacao;
        this.submissaoService = submissaoService;
    }

    @Scheduled(fixedDelay = 500)
    public void processarFila() {

        Long submissaoId = filaAvaliacao.poll();

        if (submissaoId == null) {
            return;
        }

        try {
            System.out.println(
                "[PROCESSADOR] Iniciando submissão " + submissaoId
            );

            submissaoService.iniciarProcessamento(submissaoId);

        } catch (Exception e) {

            System.err.println(
                "[ERRO] Falha ao processar submissão " +
                submissaoId + ": " + e.getMessage()
            );

            submissaoService.registrarFalha(
                submissaoId,
                e.getMessage()
            );
        }
    }
}