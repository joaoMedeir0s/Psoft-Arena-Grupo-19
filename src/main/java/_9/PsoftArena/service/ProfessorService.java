package _9.PsoftArena.service;

import org.springframework.stereotype.Service;

import _9.PsoftArena.domain.model.Matricula;
import _9.PsoftArena.domain.model.Professor;
import _9.PsoftArena.repository.ProfessorRepository;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    public Professor cadastrarProfessor(String codigoMatricula, String nome, String email) {
        Matricula matricula = new Matricula(codigoMatricula);
        if (professorRepository.existsById(matricula))
            throw new IllegalStateException("Já existe um professor cadastrado com a matrícula: " + codigoMatricula);

        Professor novoProfessor = new Professor(codigoMatricula, nome, email);
        return professorRepository.save(novoProfessor); // Usa save do JPA
    }

    
}
