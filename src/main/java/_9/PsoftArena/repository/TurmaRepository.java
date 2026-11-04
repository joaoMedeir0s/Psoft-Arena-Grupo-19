package _9.PsoftArena.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import _9.PsoftArena.domain.model.Turma;

@Repository 
public interface TurmaRepository extends JpaRepository<Turma, Long> {

    boolean existsByCodigo(String codigo);
}
