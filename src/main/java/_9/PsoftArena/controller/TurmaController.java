package _9.PsoftArena.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import _9.PsoftArena.dto.request.TurmaRequestDTO;
import _9.PsoftArena.dto.response.EstudanteResponseDTO;
import _9.PsoftArena.dto.response.TurmaResponseDTO;
import _9.PsoftArena.service.TurmaService;

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

    @GetMapping("/{codigo}")
    public ResponseEntity<TurmaResponseDTO> buscar(@PathVariable Long codigo) {
        return ResponseEntity.ok(turmaService.buscar(codigo));
    }

    @PatchMapping("/{codigo}/encerrar")
    public ResponseEntity<TurmaResponseDTO> encerrar(@PathVariable Long codigo) {
        return ResponseEntity.ok(turmaService.encerrar(codigo));
    }

    @PostMapping("/{codigo}/estudantes/{matricula}")
    public ResponseEntity<TurmaResponseDTO> matricular(@PathVariable Long codigo, @PathVariable String matricula) {
        return ResponseEntity.ok(turmaService.matricularEstudante(codigo, matricula));
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<TurmaResponseDTO> atualizar(@PathVariable Long codigo, @RequestBody TurmaRequestDTO dto) {
        return ResponseEntity.ok(turmaService.atualizar(codigo, dto));
    }

    @DeleteMapping("/{codigo}/estudantes/{matricula}")
    public ResponseEntity<TurmaResponseDTO> removerEstudante(@PathVariable Long codigo, @PathVariable String matricula) {
        return ResponseEntity.ok(turmaService.removerEstudante(codigo, matricula));
    }

    @GetMapping("/api/{codigo}/estudantes")
    public ResponseEntity<List<EstudanteResponseDTO>> listarEstudantesTurma(@PathVariable Long codigo) {
        return ResponseEntity.ok(turmaService.listarEstudantesTurma(codigo));
    }
}
