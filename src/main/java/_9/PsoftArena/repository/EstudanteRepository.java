package _9.PsoftArena.repository;

import _9.PsoftArena.domain.model.Estudante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstudanteRepository extends JpaRepository<Estudante, String> {
    boolean existsByMatricula(String matricula);
}