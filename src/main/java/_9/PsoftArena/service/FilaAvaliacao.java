
package _9.PsoftArena.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class FilaAvaliacao {

    private final BlockingQueue<Long> fila =
            new LinkedBlockingQueue<>();

    @TransactionalEventListener(
    phase = TransactionPhase.AFTER_COMMIT
)
    public void receberSubmissao(SubmissaoEnfileiradaEvent evento) {
        adicionar(evento.submissaoId());
}
    public void adicionar(Long submissaoId) {
        fila.add(submissaoId);

        System.out.println(
            "[FILA] Submissão " + submissaoId +
            " adicionada à fila de avaliação."
        );
    }

    public Long proximaSubmissao() throws InterruptedException {
        return fila.take();
    }

    public int tamanho() {
        return fila.size();
    }

    public Long poll() {
        return fila.poll();
    }
}