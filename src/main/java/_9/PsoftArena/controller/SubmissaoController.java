package _9.PsoftArena.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import _9.PsoftArena.dto.request.SubmissaoRequest;
import _9.PsoftArena.dto.response.SubmissaoResponse;
import _9.PsoftArena.service.SubmissaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/submissoes")
public class SubmissaoController {

    private final SubmissaoService submissaoService;

    public SubmissaoController(SubmissaoService submissaoService) {
        this.submissaoService = submissaoService;
    }

    /**
     * US8: Estudante envia uma nova versão de solução em Java para uma atividade aberta.
     * Cria a submissão com versão incremental e encaminha para a fila assíncrona.
     */
    @PostMapping
    public ResponseEntity<SubmissaoResponse> enviarSubmissao(@RequestBody @Valid SubmissaoRequest request) {
        SubmissaoResponse response = submissaoService.criarSubmissao(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * US9 e US10: Consulta os detalhes de uma submissão específica, incluindo
     * o status atual, histórico completo de transições e o resultado da avaliação (se concluída).
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubmissaoResponse> buscarSubmissaoPorId(@PathVariable Long id) {
        SubmissaoResponse response = submissaoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * US8 e US9: Lista todas as versões submetidas por um estudante em uma atividade,
     * permitindo acompanhar o histórico de tentativas (v1, v2, v3...).
     */
    @GetMapping
    public ResponseEntity<List<SubmissaoResponse>> listarSubmissoesPorAtividadeEEstudante(
            @RequestParam Long atividadeId,
            @RequestParam String matriculaEstudante) {
        List<SubmissaoResponse> submissoes = submissaoService
                .listarPorAtividadeEEstudante(atividadeId, matriculaEstudante);
        return ResponseEntity.ok(submissoes);
    }

    /**
     * US9 e US10 (Visão do Professor): Lista todas as submissões enviadas para uma atividade.
     */
    @GetMapping("/atividade/{atividadeId}")
    public ResponseEntity<List<SubmissaoResponse>> listarSubmissoesDaAtividade(@PathVariable Long atividadeId) {
        List<SubmissaoResponse> submissoes = submissaoService.listarPorAtividade(atividadeId);
        return ResponseEntity.ok(submissoes);
    }
}