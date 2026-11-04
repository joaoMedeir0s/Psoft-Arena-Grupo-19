package _9.PsoftArena.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import _9.PsoftArena.controller.dto.TurmaRequestDTO;
import _9.PsoftArena.controller.dto.TurmaResponseDTO;
import _9.PsoftArena.domain.model.Estudante;
import _9.PsoftArena.domain.model.Professor;
import _9.PsoftArena.domain.model.StatusTurma;
import _9.PsoftArena.domain.model.Turma;
import _9.PsoftArena.repository.EstudanteRepository;
import _9.PsoftArena.repository.ProfessorRepository;
import _9.PsoftArena.repository.TurmaRepository;

@Service
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final EstudanteRepository estudanteRepository;
    private final ProfessorRepository professorRepository;
 
    public TurmaService(TurmaRepository turmaRepository, EstudanteRepository estudanteRepository, ProfessorRepository professorRepository) {
        this.turmaRepository = turmaRepository;
        this.estudanteRepository = estudanteRepository;
        this.professorRepository = professorRepository;
    }

    public TurmaResponseDTO criar(TurmaRequestDTO turmaRequestDTO) {
        Professor professor = buscarProfessor(turmaRequestDTO.professorMatricula()); 
        if (turmaRepository.existsByCodigo(turmaRequestDTO.codigo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ja existe turma com este codigo");
        }
        Turma turma = new Turma(turmaRequestDTO.codigo(), turmaRequestDTO.nome(), turmaRequestDTO.periodoLetivo(), professor);
        return TurmaResponseDTO.de(turmaRepository.save(turma));
    }

    public List<TurmaResponseDTO> listar() {
        return turmaRepository.findAll().stream().map(TurmaResponseDTO::de).toList();
    }

    public TurmaResponseDTO buscar(Long id) {
        return TurmaResponseDTO.de(buscarTurma(id));
    }

    public TurmaResponseDTO encerrar(Long id) {
        Turma turma = buscarTurma(id);
        turma.encerrar();
        return TurmaResponseDTO.de(turmaRepository.save(turma));
    }

    private Turma buscarTurma(Long id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma nao encontrada"));
    }

    private Professor buscarProfessor(String matriculaDocente) {
        return professorRepository.findById(matriculaDocente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor nao encontrado"));
    }

    public TurmaResponseDTO matricularEstudante(Long turmaId, String matricula) {
        Turma turma = buscarTurma(turmaId);
        Estudante estudante = estudanteRepository.findById(matricula)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante nao encontrado"));

        if (turma.getStatusTurma() == StatusTurma.ENCERRADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Turma encerrada nao pode receber novos estudantes");
        }
        if (!turma.podeReceberMatriculas(estudante)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Estudante ja matriculado nesta turma");
        }

        turma.adicionarEstudante(estudante);
        return TurmaResponseDTO.de(turmaRepository.save(turma));
    }

    public TurmaResponseDTO atualizar(Long id, TurmaRequestDTO dto) {
        Turma turma = buscarTurma(id);
 
        if (!turma.getCodigo().equals(dto.codigo()) && turmaRepository.existsByCodigo(dto.codigo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ja existe turma com este codigo");
        }
 
        turma.setCodigo(dto.codigo());
        turma.setNome(dto.nome());
        turma.setPeriodoLetivo(dto.periodoLetivo());
        turma.setProfessor(buscarProfessor(dto.professorMatricula()));
        return TurmaResponseDTO.de(turmaRepository.save(turma));
    }

}
