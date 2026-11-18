package _9.PsoftArena.domain.model;

public class NotificadorTerminal implements NotificadorSubmissao {
    @Override
    public void notificarMudancaStatus(Submissao submissao) {
        System.out.printf(
            "[NOTIFICAÇÃO] Estudante: %s | Atividade: %s (v%d) | Novo Status: %s%n",
            submissao.getEstudante().getNome(),
            submissao.getAtividade().getTitulo(),
            submissao.getVersao(),
            submissao.getStatusAtual()
        );
    }
}