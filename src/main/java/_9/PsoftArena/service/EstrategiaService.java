package _9.PsoftArena.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import _9.PsoftArena.controller.dto.EstrategiaRequestDTO;
import _9.PsoftArena.controller.dto.EstrategiaResponseDTO;
import _9.PsoftArena.domain.model.Estrategia;
import _9.PsoftArena.domain.model.TipoEstrategia;
import _9.PsoftArena.repository.EstrategiaRepository;

@Service
public class EstrategiaService {
    private final EstrategiaRepository estrategiaRepository;
 
    public EstrategiaService(EstrategiaRepository estrategiaRepository) {
        this.estrategiaRepository = estrategiaRepository;
    }

    public EstrategiaResponseDTO criar(EstrategiaRequestDTO dto) {
        validar(dto);
        Estrategia estrategia = new Estrategia(dto.nome(), dto.tipoEstrategia(), dto.descricao(), dto.promptAvaliacao());
        return EstrategiaResponseDTO.de(estrategiaRepository.save(estrategia));
    }

    private void validar(EstrategiaRequestDTO dto) {
        if (dto.nome() == null || dto.nome().isBlank()
                || dto.tipoEstrategia() == null
                || dto.descricao() == null || dto.descricao().isBlank()
                || dto.promptAvaliacao() == null || dto.promptAvaliacao().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nome, tipo, descricao e prompt de avaliacao sao obrigatorios");
        }
    }

    public List<EstrategiaResponseDTO> listar(TipoEstrategia tipo) {
        List<Estrategia> estrategias = (tipo == null)
            ? estrategiaRepository.findAll()
            : estrategiaRepository.findByTipo(tipo);
        return estrategias.stream().map(EstrategiaResponseDTO::de).toList();
    }

    public EstrategiaResponseDTO buscar(Long id) {
        return EstrategiaResponseDTO.de(buscarEstrategia(id));
    }

    private Estrategia buscarEstrategia(Long id) {
        return estrategiaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estrategia nao encontrada"));
    }

    public EstrategiaResponseDTO atualizar(Long id, EstrategiaRequestDTO dto) {
        validar(dto);
        Estrategia estrategia = buscarEstrategia(id);
        estrategia.setNome(dto.nome());
        estrategia.setTipo(dto.tipoEstrategia());
        estrategia.setDescricao(dto.descricao());
        estrategia.setPromptAvaliacao(dto.promptAvaliacao());
        return EstrategiaResponseDTO.de(estrategiaRepository.save(estrategia));
    }

     public EstrategiaResponseDTO alterarPrompt(Long id, String novoPrompt) {
        if (novoPrompt == null || novoPrompt.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O prompt de avaliacao e obrigatorio");
        }
        Estrategia estrategia = buscarEstrategia(id);
        estrategia.setPromptAvaliacao(novoPrompt);
        return EstrategiaResponseDTO.de(estrategiaRepository.save(estrategia));
    }
}
