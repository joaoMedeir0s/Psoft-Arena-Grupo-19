package _9.PsoftArena.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import _9.PsoftArena.domain.model.Problema;
import _9.PsoftArena.dto.request.ProblemaRequestDTO;
import _9.PsoftArena.dto.response.ProblemaResponseDTO;
import _9.PsoftArena.service.ProblemaService;

@RestController
@RequestMapping("/api/problemas")
public class ProblemaController {

    private final ProblemaService problemaService;

    public ProblemaController(ProblemaService problemaService) {
        this.problemaService = problemaService;
    }

    @PostMapping
    public ResponseEntity<ProblemaResponseDTO> cadastrarProblema(@RequestBody ProblemaRequestDTO dto) {
        Problema problema = problemaService.cadastrarProblema(
                dto.titulo(),
                dto.descricao(),
                dto.requisitosFuncionais(),
                dto.requisitosProjeto()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ProblemaResponseDTO.de(problema));
    }

    @GetMapping
    public ResponseEntity<List<ProblemaResponseDTO>> listarProblemas() {
        List<ProblemaResponseDTO> problemas = problemaService.listarTodos()
                .stream()
                .map(ProblemaResponseDTO::de)
                .collect(Collectors.toList());
        return ResponseEntity.ok(problemas);
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<ProblemaResponseDTO> inativarProblema(@PathVariable Long id) {
        Problema problemaInativado = problemaService.inativarProblema(id);
        return ResponseEntity.ok(ProblemaResponseDTO.de(problemaInativado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerProblema(@PathVariable Long id) {
        problemaService.removerProblema(id);
        return ResponseEntity.noContent().build();
    }
}
