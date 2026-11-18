package _9.PsoftArena.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import _9.PsoftArena.domain.model.Atividade;
import _9.PsoftArena.domain.model.AvaliacaoFinal;
import _9.PsoftArena.domain.model.AvaliacaoModeloLLM;
import _9.PsoftArena.domain.model.ConfiguracaoLLM;
import _9.PsoftArena.domain.model.Estrategia;
import _9.PsoftArena.domain.model.Problema;
import _9.PsoftArena.domain.model.Submissao;
import _9.PsoftArena.repository.SubmissaoRepository;

@Service
public class AvaliadorSubmissao {

    private final SubmissaoRepository submissaoRepository;
    private final SubmissaoService submissaoService;
    private final ClienteLLM clienteLLM;

    public AvaliadorSubmissao(
            SubmissaoRepository submissaoRepository,
            SubmissaoService submissaoService,
            ClienteLLM clienteLLM) {
        this.submissaoRepository = submissaoRepository;
        this.submissaoService = submissaoService;
        this.clienteLLM = clienteLLM;
    }

    public void avaliar(Long submissaoId) {
        // Faz a transição EM_FILA → PROCESSANDO.
        submissaoService.iniciarProcessamento(submissaoId);

        try {
            Submissao submissao = submissaoRepository.findById(submissaoId)
                    .orElseThrow(() -> new IllegalStateException(
                            "Submissão não encontrada: " + submissaoId));

            List<ConfiguracaoLLM> modelos =
                    submissao.getAtividade().getConfiguracoesLLM();

            if (modelos.isEmpty()) {
                throw new IllegalStateException(
                        "A atividade não tem modelos LLM configurados.");
            }

            String prompt = montarPrompt(submissao);
            List<AvaliacaoModeloLLM> resultados = new ArrayList<>();

            for (ConfiguracaoLLM modelo : modelos) {
                ClienteLLM.ResultadoAvaliacao resultado =
                        clienteLLM.avaliar(
                                modelo,
                                prompt,
                                submissao.getCodigoFonte());

                if (resultado == null) {
                    throw new IllegalStateException(
                            "O modelo " + modelo.getNomeModelo()
                                    + " não retornou resultado.");
                }

                resultados.add(new AvaliacaoModeloLLM(
                        resultado.nota(),
                        resultado.evidencias(),
                        resultado.justificativa(),
                        true,
                        modelo));
            }

            double notaFinal = resultados.stream()
                    .mapToDouble(AvaliacaoModeloLLM::getNota)
                    .average()
                    .orElseThrow();

            String evidencias = resultados.stream()
                    .map(av -> av.getConfiguracaoLLM().getNomeModelo()
                            + ": " + av.getEvidencias())
                    .collect(Collectors.joining("\n"));

            String justificativa = resultados.stream()
                    .map(av -> av.getConfiguracaoLLM().getNomeModelo()
                            + ": " + av.getJustificativa())
                    .collect(Collectors.joining("\n"));

            AvaliacaoFinal avaliacaoFinal = new AvaliacaoFinal(
                    notaFinal,
                    evidencias,
                    justificativa,
                    resultados);

            submissaoService.concluirAvaliacao(
                    submissaoId,
                    avaliacaoFinal);

        } catch (RuntimeException exception) {
            String motivo = exception.getMessage() == null
                    ? exception.getClass().getSimpleName()
                    : exception.getMessage();

            submissaoService.registrarFalha(submissaoId, motivo);
        }
    }

    private String montarPrompt(Submissao submissao) {
        Atividade atividade = submissao.getAtividade();
        Problema problema = atividade.getProblema();
        Estrategia estrategia = atividade.getEstrategia();

        return """
                Avalie a solução Java por análise estática. Não execute o código.

                Atividade: %s
                Descrição: %s

                Problema: %s
                Descrição: %s

                Requisitos funcionais:
                %s

                Requisitos de projeto:
                %s

                Instruções da estratégia de avaliação:
                %s
                """.formatted(
                        atividade.getTitulo(),
                        atividade.getDescricao(),
                        problema.getTitulo(),
                        problema.getDescricao(),
                        problema.getRequisitosFuncionais(),
                        problema.getRequisitosProjeto(),
                        estrategia.getPromptAvaliacao());
    }
}