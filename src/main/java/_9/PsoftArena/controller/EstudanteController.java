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

import _9.PsoftArena.controller.dto.EstudanteDTO;
import _9.PsoftArena.domain.model.Estudante;
import _9.PsoftArena.service.EstudanteService;

@RestController
@RequestMapping("/api/estudantes")
public class EstudanteController {

    private final EstudanteService estudanteService;

    public EstudanteController(EstudanteService estudanteService) {
        this.estudanteService = estudanteService;
    }

    @PostMapping
    public ResponseEntity<EstudanteDTO> cadastrarEstudante(@RequestBody EstudanteDTO dto) {
        Estudante estudante = estudanteService.cadastrarEstudante(
                dto.getMatricula(),
                dto.getNome(),
                dto.getEmail()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(new EstudanteDTO(estudante));
    }

    @PutMapping("/{matricula}")
    public ResponseEntity<EstudanteDTO> editarEstudante(
            @PathVariable String matricula,
            @RequestBody EstudanteDTO dto) {
        Estudante estudanteAtualizado = estudanteService.editarEstudante(
                matricula,
                dto.getNome(),
                dto.getEmail()
        );
        return ResponseEntity.ok(new EstudanteDTO(estudanteAtualizado));
    }

    @GetMapping
    public ResponseEntity<List<EstudanteDTO>> listarEstudantes() {
        List<EstudanteDTO> estudantes = estudanteService.listarTodos()
                .stream()
                .map(EstudanteDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estudantes);
    }
}
