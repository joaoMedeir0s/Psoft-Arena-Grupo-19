package _9.PsoftArena.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import _9.PsoftArena.domain.model.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, String> {

}
