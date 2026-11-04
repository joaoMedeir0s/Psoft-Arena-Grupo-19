package _9.PsoftArena.service;

import _9.PsoftArena.domain.model.Problema;
import _9.PsoftArena.repository.ProblemaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProblemaService {
    private final ProblemaRepository repository;

    public ProblemaService(ProblemaRepository repository) {
        this.repository = repository;
    }

    public Problema cadastrarProblema(String titulo, String descricao, String reqFunc, String reqProj) {
        String id = UUID.randomUUID().toString();
        Problema novoProblema = new Problema(id, titulo, descricao, reqFunc, reqProj);
        return repository.save(novoProblema); // Usa save do JPA
    }

    public Problema inativarProblema(String id) {
        Problema problema = repository.findById(id) // Usa findById do JPA
                .orElseThrow(() -> new IllegalArgumentException("Problema não encontrado"));
        problema.inativar();
        return repository.save(problema);
    }

    public void removerProblema(String id) {
        Problema problema = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Problema não encontrado"));

        if (!problema.podeSerRemovido()) {
            throw new IllegalStateException("Problemas que já foram utilizados em atividades não podem ser removidos.");
        }

        repository.deleteById(id); // Usa deleteById do JPA
    }

    public List<Problema> listarTodos() {
        return repository.findAll(); // Usa findAll do JPA
    }

}