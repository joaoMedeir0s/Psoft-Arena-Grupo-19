package _9.PsoftArena.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ConfiguracaoLLMTest {

    @Test
    void deveCriarConfiguracaoLLMValida() {
        ConfiguracaoLLM config = new ConfiguracaoLLM(
                "grok-2-latest",
                "https://api.x.ai/v1/chat/completions",
                "xai-chave-secreta-123",
                true
        );

        assertEquals("grok-2-latest", config.getNomeModelo());
        assertEquals("https://api.x.ai/v1/chat/completions", config.getUrlApi());
        assertEquals("xai-chave-secreta-123", config.acessarChaveApiSegura());
        assertTrue(config.getPadrao());
    }

    @Test
    void deveRejeitarNomeModeloNuloOuEmBranco() {
        assertThrows(IllegalArgumentException.class, () ->
            new ConfiguracaoLLM("", "https://api.x.ai/v1/chat/completions", "xai-chave", true)
        );

        assertThrows(IllegalArgumentException.class, () ->
            new ConfiguracaoLLM("   ", "https://api.x.ai/v1/chat/completions", "xai-chave", true)
        );

        assertThrows(IllegalArgumentException.class, () ->
            new ConfiguracaoLLM(null, "https://api.x.ai/v1/chat/completions", "xai-chave", true)
        );
    }

    @Test
    void deveRejeitarUrlApiNulaOuEmBranco() {
        assertThrows(IllegalArgumentException.class, () ->
            new ConfiguracaoLLM("grok-2-latest", "", "xai-chave", true)
        );

        assertThrows(IllegalArgumentException.class, () ->
            new ConfiguracaoLLM("grok-2-latest", "   ", "xai-chave", true)
        );

        assertThrows(IllegalArgumentException.class, () ->
            new ConfiguracaoLLM("grok-2-latest", null, "xai-chave", true)
        );
    }

    @Test
    void deveRejeitarChaveApiNulaOuEmBranco() {
        assertThrows(IllegalArgumentException.class, () ->
            new ConfiguracaoLLM("grok-2-latest", "https://api.x.ai/v1/chat/completions", "", true)
        );

        assertThrows(IllegalArgumentException.class, () ->
            new ConfiguracaoLLM("grok-2-latest", "https://api.x.ai/v1/chat/completions", "   ", true)
        );

        assertThrows(IllegalArgumentException.class, () ->
            new ConfiguracaoLLM("grok-2-latest", "https://api.x.ai/v1/chat/completions", null, true)
        );
    }

    @Test
    void naoDeveExporChaveApiNoToString() {
        String chaveSecreta = "xai-chave-super-secreta-999";
        ConfiguracaoLLM config = new ConfiguracaoLLM(
                "grok-2-latest",
                "https://api.x.ai/v1/chat/completions",
                chaveSecreta,
                false
        );

        assertFalse(config.toString().contains(chaveSecreta));
    }
}