package _9.PsoftArena.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import _9.PsoftArena.domain.model.Atividade;
import _9.PsoftArena.domain.model.Estudante;
import _9.PsoftArena.domain.model.StatusSubmissao;
import _9.PsoftArena.domain.model.Submissao;

@Repository
public interface SubmissaoRepository extends JpaRepository<Submissao, Long> {

    /**
     * US8: Conta quantas submissões o estudante já fez nesta atividade
     * para calcular automaticamente o número da próxima versão (count + 1).
     */
    int countByAtividadeAndEstudante(Atividade atividade, Estudante estudante);

    /**
     * US8 e US9: Lista todas as versões submetidas por um estudante em uma atividade,
     * ordenadas da versão 1 em diante.
     */
    List<Submissao> findByAtividadeAndEstudanteOrderByVersaoAsc(Atividade atividade, Estudante estudante);

    /**
     * US9 e US10 (Visão do Professor): Lista todas as submissões enviadas
     * para uma atividade específica, ordenadas por data de envio.
     */
    List<Submissao> findByAtividadeOrderByDataHoraSubmissaoDesc(Atividade atividade);

    /**
     * Consulta auxiliar para recuperação ou monitoramento da fila (ex: buscar submissões EM_FILA).
     */
    List<Submissao> findByStatusAtual(StatusSubmissao statusAtual);
}
