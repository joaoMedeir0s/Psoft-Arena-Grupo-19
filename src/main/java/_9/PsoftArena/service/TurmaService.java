package _9.PsoftArena.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import _9.PsoftArena.domain.model.Estudante;
import _9.PsoftArena.domain.model.Matricula;
import _9.PsoftArena.domain.model.Professor;
import _9.PsoftArena.domain.model.StatusTurma;
import _9.PsoftArena.domain.model.Turma;
import _9.PsoftArena.dto.request.TurmaRequestDTO;
import _9.PsoftArena.dto.response.EstudanteResponseDTO;
import _9.PsoftArena.dto.response.TurmaResponseDTO;
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

    // O que vai impedir de se criarem turmas iguais, sendo que o código é gerado automaticamente?
    // Nome da turma + período letivo 
    public TurmaResponseDTO criar(TurmaRequestDTO turmaRequestDTO) {
        Professor professor = buscarProfessor(turmaRequestDTO.matriculaDocente()); 
        if (turmaRepository.existsByNomeAndPeriodoLetivo(turmaRequestDTO.nome(), turmaRequestDTO.periodoLetivo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Turma com o mesmo nome e período letivo já existe");
        }
        Turma turma = new Turma(turmaRequestDTO.nome(), turmaRequestDTO.periodoLetivo(), professor);
        return TurmaResponseDTO.de(turmaRepository.save(turma));
    }

    @Transactional(readOnly = true)
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

    private Professor buscarProfessor(String matriculaDocente) {
        return professorRepository.findById(new Matricula(matriculaDocente))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor nao encontrado"));
    }

    public TurmaResponseDTO matricularEstudante(Long codigo, String codigoMatricula) {
        Turma turma = buscarTurma(codigo);
        Estudante estudante = buscarEstudante(codigoMatricula);

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

    private Estudante buscarEstudante(String codigoMatricula) {
        if (codigoMatricula == null || codigoMatricula.isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matricula nao pode ser nulo");
        }

        return estudanteRepository.findById(new Matricula(codigoMatricula))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante nao encontrado"));
    }

    private Turma buscarTurma(Long id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID da turma nao pode ser nulo");
        }
        return turmaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma nao encontrada"));
    }

    public TurmaResponseDTO atualizar(Long codigo, TurmaRequestDTO dto) {
        Turma turma = buscarTurma(codigo);
 
        turma.setNome(dto.nome());
        turma.setPeriodoLetivo(dto.periodoLetivo());
        turma.setProfessor(buscarProfessor(dto.matriculaDocente()));
        return TurmaResponseDTO.de(turmaRepository.save(turma));
    }

    public TurmaResponseDTO removerEstudante(Long codigo, String codigoMatricula) {
        Turma turma = buscarTurma(codigo);
        Estudante estudante = buscarEstudante(codigoMatricula);

        if (turma.getStatusTurma() == StatusTurma.ENCERRADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Turma encerrada nao pode ter estudantes removidos");
        }

        if (!turma.getEstudantes().contains(estudante)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Estudante nao esta associado a esta turma");
        }

        turma.removerEstudante(estudante);
        return TurmaResponseDTO.de(turmaRepository.save(turma));
    }

    @Transactional(readOnly = true)
    public List<EstudanteResponseDTO> listarEstudantesTurma(Long codigo) {
        Turma turma = buscarTurma(codigo);
        return turma.getEstudantes().stream()
            .map(EstudanteResponseDTO::de)
            .toList();
    }
}
