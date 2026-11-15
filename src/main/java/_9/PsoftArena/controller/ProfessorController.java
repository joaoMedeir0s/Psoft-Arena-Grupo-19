package _9.PsoftArena.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import _9.PsoftArena.domain.model.Professor;
import _9.PsoftArena.dto.request.ProfessorRequestDTO;
import _9.PsoftArena.dto.response.ProfessorResponseDTO;
import _9.PsoftArena.service.ProfessorService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("/api/professores")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService){
        this.professorService = professorService;
    }

    @PostMapping
    public ResponseEntity<ProfessorResponseDTO> cadastraProfessor(@RequestBody ProfessorRequestDTO dto) {
        Professor professor = professorService.cadastrarProfessor(
           dto.codigoMatriculaDocente(),
           dto.nome(),
           dto.email());

        return ResponseEntity.status(HttpStatus.CREATED).body(ProfessorResponseDTO.de(professor));

    }
}
