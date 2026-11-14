package _9.PsoftArena.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import _9.PsoftArena.domain.model.Estudante;
import _9.PsoftArena.dto.request.EstudanteCreateRequestDTO;
import _9.PsoftArena.dto.request.EstudanteUpdateRequestDTO;
import _9.PsoftArena.dto.response.EstudanteResponseDTO;
import _9.PsoftArena.service.EstudanteService;

@RestController
@RequestMapping("/api/estudantes")
public class EstudanteController {

    private final EstudanteService estudanteService;

    public EstudanteController(EstudanteService estudanteService) {
        this.estudanteService = estudanteService;
    }

    @PostMapping
    public ResponseEntity<EstudanteResponseDTO> cadastrarEstudante(@RequestBody EstudanteCreateRequestDTO dto) {
        Estudante estudante = estudanteService.cadastrarEstudante(
                dto.codigoMatricula(),
                dto.nome(),
                dto.email()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(EstudanteResponseDTO.de(estudante));
    }

    @PutMapping("/{matricula}")
    public ResponseEntity<EstudanteResponseDTO> editarEstudante(
            @PathVariable String matricula,
            @RequestBody EstudanteUpdateRequestDTO dto) {
        Estudante estudanteAtualizado = estudanteService.editarEstudante(
                matricula,
                dto.nome(),
                dto.email()
        );
        return ResponseEntity.ok(EstudanteResponseDTO.de(estudanteAtualizado));
    }

    @GetMapping
    public ResponseEntity<List<EstudanteResponseDTO>> listarEstudantes() {
        List<EstudanteResponseDTO> estudantes = estudanteService.listarTodos()
                .stream()
                .map(EstudanteResponseDTO::de)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estudantes);
    }
}
