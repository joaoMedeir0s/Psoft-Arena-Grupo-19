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

import _9.PsoftArena.controller.dto.ProblemaDTO;
import _9.PsoftArena.domain.model.Problema;
import _9.PsoftArena.service.ProblemaService;

@RestController
@RequestMapping("/api/problemas")
public class ProblemaController {

    private final ProblemaService problemaService;

    public ProblemaController(ProblemaService problemaService) {
        this.problemaService = problemaService;
    }

    @PostMapping
    public ResponseEntity<ProblemaDTO> cadastrarProblema(@RequestBody ProblemaDTO dto) {
        Problema problema = problemaService.cadastrarProblema(
                dto.getTitulo(),
                dto.getDescricao(),
                dto.getRequisitosFuncionais(),
                dto.getRequisitosProjeto()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProblemaDTO(problema));
    }

    @GetMapping
    public ResponseEntity<List<ProblemaDTO>> listarProblemas() {
        List<ProblemaDTO> problemas = problemaService.listarTodos()
                .stream()
                .map(ProblemaDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(problemas);
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<ProblemaDTO> inativarProblema(@PathVariable String id) {
        Problema problemaInativado = problemaService.inativarProblema(id);
        return ResponseEntity.ok(new ProblemaDTO(problemaInativado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerProblema(@PathVariable String id) {
        problemaService.removerProblema(id);
        return ResponseEntity.noContent().build();
    }
}
