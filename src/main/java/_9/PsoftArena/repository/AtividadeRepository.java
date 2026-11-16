package _9.PsoftArena.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import _9.PsoftArena.domain.model.Atividade;
import _9.PsoftArena.domain.model.Estudante;
import _9.PsoftArena.domain.model.StatusAtividade;
import _9.PsoftArena.domain.model.Turma;

@Repository
public interface AtividadeRepository extends JpaRepository<Atividade, Long> {

    /**
     * US7: Retorna todas as atividades (abertas ou concluídas) associadas
     * às turmas das quais o estudante participa.
     */
    List<Atividade> findByTurmaEstudantesContaining(Estudante estudante);

    /**
     * Consulta auxiliar por turma específica.
     */
    List<Atividade> findByTurma(Turma turma);

    /**
     * Consulta auxiliar por status (ABERTA ou CONCLUIDA).
     */
    List<Atividade> findByStatus(StatusAtividade status);
}
