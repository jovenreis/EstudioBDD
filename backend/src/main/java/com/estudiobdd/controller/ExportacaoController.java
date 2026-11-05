package com.estudiobdd.controller;

import com.estudiobdd.service.ExportacaoService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
public class ExportacaoController {

    private static final MediaType TEXTO_UTF8 = new MediaType("text", "plain", StandardCharsets.UTF_8);

    private final ExportacaoService exportacaoService;

    public ExportacaoController(ExportacaoService exportacaoService) {
        this.exportacaoService = exportacaoService;
    }

    // devolve o texto do .feature; o cabeçalho faz o navegador baixar como arquivo
    @GetMapping("/api/historias/{id}/feature")
    public ResponseEntity<String> exportar(@PathVariable Long id) {
        String feature = exportacaoService.exportar(id);
        ContentDisposition anexo = ContentDisposition.attachment()
                .filename(exportacaoService.nomeArquivo(id), StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(TEXTO_UTF8)
                .header(HttpHeaders.CONTENT_DISPOSITION, anexo.toString())
                .body(feature);
    }
}
