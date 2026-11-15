package _9.PsoftArena.repository;

import _9.PsoftArena.domain.model.Estudante;
import _9.PsoftArena.domain.model.Matricula;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstudanteRepository extends JpaRepository<Estudante, Matricula> {
}