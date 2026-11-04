package _9.PsoftArena.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import _9.PsoftArena.controller.dto.TurmaRequestDTO;
import _9.PsoftArena.controller.dto.TurmaResponseDTO;
import _9.PsoftArena.service.TurmaService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController 
@RequestMapping ("/api/turmas")
public class TurmaController {

    private final TurmaService turmaService;

    public TurmaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @PostMapping
    public ResponseEntity<TurmaResponseDTO> criar(@RequestBody TurmaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(turmaService.criar(dto));
    }   
    
    @GetMapping
    public ResponseEntity<List<TurmaResponseDTO>> listar() {
        return ResponseEntity.ok(turmaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(turmaService.buscar(id));
    }

    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<TurmaResponseDTO> encerrar(@PathVariable Long id) {
        return ResponseEntity.ok(turmaService.encerrar(id));
    }

    @PostMapping("/{id}/estudantes/{matricula}")
    public ResponseEntity<TurmaResponseDTO> matricular(@PathVariable Long id,
                                                   @PathVariable String matricula) {
        return ResponseEntity.ok(turmaService.matricularEstudante(id, matricula));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurmaResponseDTO> atualizar(@PathVariable Long id,
                                                      @RequestBody TurmaRequestDTO dto) {
        return ResponseEntity.ok(turmaService.atualizar(id, dto));
    }
}
