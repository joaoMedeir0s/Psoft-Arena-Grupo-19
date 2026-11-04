package _9.PsoftArena.service;

import _9.PsoftArena.domain.model.Estudante;
import _9.PsoftArena.repository.EstudanteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstudanteService {
    private final EstudanteRepository repository;

    public EstudanteService(EstudanteRepository repository) {
        this.repository = repository;
    }

    public Estudante cadastrarEstudante(String matricula, String nome, String email) {
        if (repository.existsById(matricula)) { // Ou repository.existsByMatricula(matricula)
            throw new IllegalStateException("Já existe um estudante cadastrado com a matrícula: " + matricula);
        }
        Estudante estudante = new Estudante(matricula, nome, email);
        return repository.save(estudante); // Usa save do JPA
    }

    public Estudante editarEstudante(String matricula, String novoNome, String novoEmail) {
        Estudante estudante = repository.findById(matricula) // Usa findById do JPA
                .orElseThrow(() -> new IllegalArgumentException("Estudante não encontrado"));

        estudante.setNome(novoNome);
        estudante.setEmail(novoEmail);
        return repository.save(estudante);
    }

    public List<Estudante> listarTodos() {
        return repository.findAll(); // Usa findAll do JPA
    }
}