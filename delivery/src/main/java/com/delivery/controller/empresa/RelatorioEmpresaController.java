package com.delivery.controller.empresa;

import com.delivery.dto.empresa.RelatorioDTO;
import com.delivery.service.empresa.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/empresa/relatorios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RelatorioEmpresaController {

    private final RelatorioService relatorioService;

    @GetMapping("/mensal")
    public ResponseEntity<RelatorioDTO> obterRelatorioMensal(
            Authentication authentication,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") int mes,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int ano) {

        if (mes == 0) mes = LocalDate.now().getMonthValue();
        if (ano == 0) ano = LocalDate.now().getYear();

        RelatorioDTO relatorio = relatorioService.gerarRelatorioMensal(
                authentication.getName(), mes, ano);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/anual")
    public ResponseEntity<RelatorioDTO> obterRelatorioAnual(
            Authentication authentication,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int ano) {

        if (ano == 0) ano = LocalDate.now().getYear();

        RelatorioDTO relatorio = relatorioService.gerarRelatorioAnual(
                authentication.getName(), ano);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/periodo")
    public ResponseEntity<RelatorioDTO> obterRelatorioPorPeriodo(
            Authentication authentication,
            @RequestParam String dataInicio,
            @RequestParam String dataFim) {

        RelatorioDTO relatorio = relatorioService.gerarRelatorioPorPeriodo(
                authentication.getName(), dataInicio, dataFim);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/vendas")
    public ResponseEntity<Map<String, Object>> obterRelatorioVendas(
            Authentication authentication,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") int mes,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int ano) {

        if (mes == 0) mes = LocalDate.now().getMonthValue();
        if (ano == 0) ano = LocalDate.now().getYear();

        Map<String, Object> relatorio = relatorioService.gerarRelatorioVendas(
                authentication.getName(), mes, ano);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<Map<String, Object>> obterProdutosMaisVendidos(
            Authentication authentication,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") int mes,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int ano,
            @RequestParam(defaultValue = "10") int limite) {

        if (mes == 0) mes = LocalDate.now().getMonthValue();
        if (ano == 0) ano = LocalDate.now().getYear();

        Map<String, Object> relatorio = relatorioService.obterProdutosMaisVendidos(
                authentication.getName(), mes, ano, limite);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/clientes-frequentes")
    public ResponseEntity<Map<String, Object>> obterClientesFrequentes(
            Authentication authentication,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") int mes,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int ano,
            @RequestParam(defaultValue = "10") int limite) {

        if (mes == 0) mes = LocalDate.now().getMonthValue();
        if (ano == 0) ano = LocalDate.now().getYear();

        Map<String, Object> relatorio = relatorioService.obterClientesFrequentes(
                authentication.getName(), mes, ano, limite);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> obterDadosDashboard(
            Authentication authentication) {

        Map<String, Object> dashboard = relatorioService.gerarDadosDashboard(
                authentication.getName());
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/comparativo")
    public ResponseEntity<Map<String, Object>> obterRelatorioComparativo(
            Authentication authentication,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") int mesAtual,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int anoAtual) {

        if (mesAtual == 0) mesAtual = LocalDate.now().getMonthValue();
        if (anoAtual == 0) anoAtual = LocalDate.now().getYear();

        Map<String, Object> relatorio = relatorioService.gerarRelatorioComparativo(
                authentication.getName(), mesAtual, anoAtual);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarRelatorioPDF(
            Authentication authentication,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") int mes,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int ano) {

        if (mes == 0) mes = LocalDate.now().getMonthValue();
        if (ano == 0) ano = LocalDate.now().getYear();

        byte[] pdf = relatorioService.exportarRelatorioPDF(
                authentication.getName(), mes, ano);

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=relatorio.pdf")
                .body(pdf);
    }
}
