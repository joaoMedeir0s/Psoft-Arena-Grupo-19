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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import _9.PsoftArena.controller.dto.EstrategiaRequestDTO;
import _9.PsoftArena.controller.dto.EstrategiaResponseDTO;
import _9.PsoftArena.domain.model.TipoEstrategia;
import _9.PsoftArena.service.EstrategiaService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/estrategias")
public class EstrategiaController {
    
    private final EstrategiaService estrategiaService;
 
    public EstrategiaController(EstrategiaService estrategiaService) {
        this.estrategiaService = estrategiaService;
    }

    @PostMapping
    public ResponseEntity<EstrategiaResponseDTO> criar(@RequestBody EstrategiaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estrategiaService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<EstrategiaResponseDTO>> listar( @RequestParam(required = false) TipoEstrategia tipo) {
        return ResponseEntity.ok(estrategiaService.listar(tipo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstrategiaResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(estrategiaService.buscar(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstrategiaResponseDTO> atualizar(@PathVariable Long id, @RequestBody EstrategiaRequestDTO dto) {
        return ResponseEntity.ok(estrategiaService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/prompt")
    public ResponseEntity<EstrategiaResponseDTO> alterarPrompt(@PathVariable Long id, @RequestBody String novoPrompt) {
        return ResponseEntity.ok(estrategiaService.alterarPrompt(id, novoPrompt));
    }
}
