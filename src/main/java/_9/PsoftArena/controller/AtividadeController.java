package _9.PsoftArena.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import _9.PsoftArena.service.AtividadeService;
import _9.PsoftArena.dto.response.AtividadeResponse;
import _9.PsoftArena.dto.request.ConfiguracaoLLMRequest;
import _9.PsoftArena.dto.request.AtividadeRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/atividades")
public class AtividadeController {

    private final AtividadeService atividadeService;

    public AtividadeController(AtividadeService atividadeService) {
        this.atividadeService = atividadeService;
    }

    /**
     * US6: Professor cria uma atividade para uma turma ativa com problema ativo,
     * uma estratégia de avaliação e um LLM padrão.
     */
    @PostMapping
    public ResponseEntity<AtividadeResponse> criarAtividade(@RequestBody @Valid AtividadeRequest request) {
        AtividadeResponse response = atividadeService.criarAtividade(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * US6: Professor encerra uma atividade, alterando seu status para CONCLUIDA
     * e bloqueando novas submissões.
     */
    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<AtividadeResponse> encerrarAtividade(@PathVariable Long id) {
        AtividadeResponse response = atividadeService.encerrarAtividade(id);
        return ResponseEntity.ok(response);
    }

    /**
     * US10: Professor adiciona um LLM secundário para avaliação multi-modelo.
     */
    @PostMapping("/{id}/llms")
    public ResponseEntity<AtividadeResponse> adicionarLLMSecundario(
            @PathVariable Long id,
            @RequestBody @Valid ConfiguracaoLLMRequest request) {
        AtividadeResponse response = atividadeService.adicionarLLMSecundario(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * US7: Estudante consulta todas as atividades das turmas das quais participa.
     */
    @GetMapping("/estudante/{matricula}")
    public ResponseEntity<List<AtividadeResponse>> listarAtividadesDoEstudante(@PathVariable String matricula) {
        List<AtividadeResponse> atividades = atividadeService.listarAtividadesPorEstudante(matricula);
        return ResponseEntity.ok(atividades);
    }

    /**
     * US7: Estudante consulta os detalhes completos de uma atividade específica
     * (bloqueia acesso caso o estudante não faça parte da turma da atividade).
     */
    @GetMapping("/{id}")
    public ResponseEntity<AtividadeResponse> buscarAtividadeParaEstudante(
            @PathVariable Long id,
            @RequestParam String matriculaEstudante) {
        AtividadeResponse response = atividadeService.buscarAtividadeParaEstudante(id, matriculaEstudante);
        return ResponseEntity.ok(response);
    }
}
