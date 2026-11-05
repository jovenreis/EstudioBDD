package com.estudiobdd.service;

import com.estudiobdd.model.Cenario;
import com.estudiobdd.model.HistoriaUsuario;
import com.estudiobdd.model.Passo;
import com.estudiobdd.model.Sugestao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;

/**
 * Regra 5: exporta a história em Gherkin (# language: pt) somente se ela tiver
 * ao menos um cenário e nenhuma sugestão pendente.
 */
@Service
public class ExportacaoService {

    private final HistoriaService historiaService;

    public ExportacaoService(HistoriaService historiaService) {
        this.historiaService = historiaService;
    }

    @Transactional(readOnly = true)
    public String exportar(Long historiaId) {
        HistoriaUsuario historia = historiaService.buscar(historiaId);
        validarExportacao(historia);
        return gerarFeature(historia);
    }

    @Transactional(readOnly = true)
    public String nomeArquivo(Long historiaId) {
        return nomeArquivo(historiaService.buscar(historiaId).getTitulo());
    }

    private void validarExportacao(HistoriaUsuario historia) {
        if (historia.getCenarios().isEmpty()) {
            throw new RegraNegocioException("A história precisa ter ao menos um cenário para ser exportada");
        }

        long pendentes = historia.getSugestoes().stream().filter(Sugestao::isPendente).count();
        if (pendentes > 0) {
            throw new RegraNegocioException("A história tem " + pendentes
                    + (pendentes == 1 ? " sugestão pendente" : " sugestões pendentes")
                    + ". Aceite ou rejeite antes de exportar");
        }
    }

    String gerarFeature(HistoriaUsuario historia) {
        StringBuilder sb = new StringBuilder();
        sb.append("# language: pt\n");
        sb.append("Funcionalidade: ").append(historia.getTitulo()).append('\n');
        sb.append("  Como ").append(historia.getPapel()).append('\n');
        sb.append("  Quero ").append(historia.getObjetivo()).append('\n');
        sb.append("  Para ").append(historia.getBeneficio()).append('\n');

        for (Cenario cenario : historia.getCenarios()) {
            sb.append('\n');
            sb.append("  Cenário: ").append(cenario.getTitulo()).append('\n');
            for (Passo passo : cenario.getPassos()) {
                sb.append("    ").append(passo.getTipo().getPalavraChave())
                        .append(' ').append(passo.getTexto()).append('\n');
            }
        }
        return sb.toString();
    }

    // "Adicionar ao carrinho" -> "adicionar-ao-carrinho.feature"
    static String nomeArquivo(String titulo) {
        String semAcento = Normalizer.normalize(titulo, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        String nome = semAcento.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        return (nome.isEmpty() ? "historia" : nome) + ".feature";
    }
}
