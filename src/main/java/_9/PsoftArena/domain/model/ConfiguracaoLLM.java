package _9.PsoftArena.domain.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "tb_configuracao_llm")
public class ConfiguracaoLLM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(nullable = false)
    @Getter
    private String nomeModelo;

    @Column(nullable = false)
    @Getter
    private String urlApi;

    // Sem @Getter público para proteger a credencial (US6)
    @Column(nullable = false)
    private String chaveApi;

    @Column(nullable = false)
    @Getter
    private boolean padrao;

    protected ConfiguracaoLLM() {
    }

    public ConfiguracaoLLM(String nomeModelo, String urlApi, String chaveApi, boolean padrao) {
        if (nomeModelo == null || nomeModelo.isBlank()) {
            throw new IllegalArgumentException("Nome do modelo LLM é obrigatório.");
        }
        if (urlApi == null || urlApi.isBlank()) {
            throw new IllegalArgumentException("URL da API é obrigatória.");
        }
        if (chaveApi == null || chaveApi.isBlank()) {
            throw new IllegalArgumentException("Chave da API é obrigatória.");
        }
        this.nomeModelo = nomeModelo;
        this.urlApi = urlApi;
        this.chaveApi = chaveApi;
        this.padrao = padrao;
    }

    public boolean getPadrao() {
        return padrao;
    }

    // Acesso restrito ao pacote de domínio para integração com ClienteLLM
    String acessarChaveApiSegura() {
        return chaveApi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfiguracaoLLM that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}