package com.delivery.service.empresa;

import com.delivery.dto.empresa.RelatorioDTO;
import com.delivery.entity.Empresa;
import com.delivery.exception.NotFoundException;
import com.delivery.repository.EmpresaRepository;
import com.delivery.repository.FeedbackRepository;
import com.delivery.repository.PedidoRepository;
import com.delivery.repository.ProdutoRepository;
import com.delivery.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final EmpresaRepository empresaRepository;
    private final PedidoRepository pedidoRepository;
    private final FeedbackRepository feedbackRepository;
    private final ProdutoRepository produtoRepository;

    public RelatorioDTO gerarRelatorioMensal(String emailEmpresa, int mes, int ano) {
        Empresa empresa = buscarEmpresaPorEmail(emailEmpresa);

        YearMonth yearMonth = YearMonth.of(ano, mes);
        LocalDateTime inicioMes = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime fimMes = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        return criarRelatorio(empresa, inicioMes, fimMes, false);
    }

    public RelatorioDTO gerarRelatorioAnual(String emailEmpresa, int ano) {
        Empresa empresa = buscarEmpresaPorEmail(emailEmpresa);

        LocalDateTime inicioAno = LocalDateTime.of(ano, 1, 1, 0, 0, 0);
        LocalDateTime fimAno = LocalDateTime.of(ano, 12, 31, 23, 59, 59);

        return criarRelatorio(empresa, inicioAno, fimAno, true);
    }

    public RelatorioDTO gerarRelatorioPorPeriodo(String emailEmpresa, String dataInicio, String dataFim) {
        Empresa empresa = buscarEmpresaPorEmail(emailEmpresa);

        LocalDateTime inicio = DateUtils.parseDate(dataInicio, DateUtils.FORMATTER_YYYY_MM_DD).atStartOfDay();
        LocalDateTime fim = DateUtils.parseDate(dataFim, DateUtils.FORMATTER_YYYY_MM_DD).atTime(23, 59, 59);

        return criarRelatorio(empresa, inicio, fim, false);
    }

    public Map<String, Object> gerarRelatorioVendas(String emailEmpresa, int mes, int ano) {
        Empresa empresa = buscarEmpresaPorEmail(emailEmpresa);

        YearMonth yearMonth = YearMonth.of(ano, mes);
        LocalDateTime inicioMes = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime fimMes = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        Map<String, Object> relatorio = new HashMap<>();

        // Vendas do período
        BigDecimal vendas = pedidoRepository.sumTotalByEmpresaIdAndPeriodo(empresa.getId(), inicioMes, fimMes);
        Long quantidadePedidos = pedidoRepository.countByEmpresaIdAndPeriodo(empresa.getId(), inicioMes, fimMes);

        relatorio.put("vendas", vendas != null ? vendas : BigDecimal.ZERO);
        relatorio.put("quantidadePedidos", quantidadePedidos);
        relatorio.put("ticketMedio", quantidadePedidos > 0 ?
                (vendas != null ? vendas.divide(BigDecimal.valueOf(quantidadePedidos), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO)
                : BigDecimal.ZERO);

        return relatorio;
    }

    public Map<String, Object> obterProdutosMaisVendidos(String emailEmpresa, int mes, int ano, int limite) {
        Empresa empresa = buscarEmpresaPorEmail(emailEmpresa);

        Map<String, Object> relatorio = new HashMap<>();
        // Implementação básica - pode ser expandida com query específica
        relatorio.put("produtosMaisVendidos", new java.util.ArrayList<>());
        relatorio.put("periodo", mes + "/" + ano);
        relatorio.put("limite", limite);

        return relatorio;
    }

    public Map<String, Object> obterClientesFrequentes(String emailEmpresa, int mes, int ano, int limite) {
        Empresa empresa = buscarEmpresaPorEmail(emailEmpresa);

        Map<String, Object> relatorio = new HashMap<>();
        // Implementação básica - pode ser expandida com query específica
        relatorio.put("clientesFrequentes", new java.util.ArrayList<>());
        relatorio.put("periodo", mes + "/" + ano);
        relatorio.put("limite", limite);

        return relatorio;
    }

    public Map<String, Object> gerarDadosDashboard(String emailEmpresa) {
        Empresa empresa = buscarEmpresaPorEmail(emailEmpresa);

        Map<String, Object> dashboard = new HashMap<>();

        // Dados do mês atual
        LocalDate hoje = LocalDate.now();
        YearMonth mesAtual = YearMonth.from(hoje);
        LocalDateTime inicioMes = mesAtual.atDay(1).atStartOfDay();
        LocalDateTime fimMes = mesAtual.atEndOfMonth().atTime(23, 59, 59);

        // Vendas do mês
        BigDecimal vendasMes = pedidoRepository.sumTotalByEmpresaIdAndPeriodo(empresa.getId(), inicioMes, fimMes);
        Long pedidosMes = pedidoRepository.countByEmpresaIdAndPeriodo(empresa.getId(), inicioMes, fimMes);

        dashboard.put("vendasMes", vendasMes != null ? vendasMes : BigDecimal.ZERO);
        dashboard.put("pedidosMes", pedidosMes);

        // Produtos
        Long totalProdutos = produtoRepository.countByEmpresaId(empresa.getId());
        Long produtosAtivos = produtoRepository.countByEmpresaIdAndAtivoTrue(empresa.getId());

        dashboard.put("totalProdutos", totalProdutos);
        dashboard.put("produtosAtivos", produtosAtivos);

        // Avaliações
        Double avaliacaoMedia = feedbackRepository.findAvaliacaoMediaByEmpresaId(empresa.getId());
        Long totalAvaliacoes = feedbackRepository.countByEmpresaId(empresa.getId());

        dashboard.put("avaliacaoMedia", avaliacaoMedia != null ? avaliacaoMedia : 0.0);
        dashboard.put("totalAvaliacoes", totalAvaliacoes);

        return dashboard;
    }

    public Map<String, Object> gerarRelatorioComparativo(String emailEmpresa, int mesAtual, int anoAtual) {
        Empresa empresa = buscarEmpresaPorEmail(emailEmpresa);

        Map<String, Object> comparativo = new HashMap<>();

        // Mês atual
        YearMonth mesAtualYM = YearMonth.of(anoAtual, mesAtual);
        LocalDateTime inicioMesAtual = mesAtualYM.atDay(1).atStartOfDay();
        LocalDateTime fimMesAtual = mesAtualYM.atEndOfMonth().atTime(23, 59, 59);

        // Mês anterior
        YearMonth mesAnteriorYM = mesAtualYM.minusMonths(1);
        LocalDateTime inicioMesAnterior = mesAnteriorYM.atDay(1).atStartOfDay();
        LocalDateTime fimMesAnterior = mesAnteriorYM.atEndOfMonth().atTime(23, 59, 59);

        // Dados mês atual
        BigDecimal vendasAtual = pedidoRepository.sumTotalByEmpresaIdAndPeriodo(empresa.getId(), inicioMesAtual, fimMesAtual);
        Long pedidosAtual = pedidoRepository.countByEmpresaIdAndPeriodo(empresa.getId(), inicioMesAtual, fimMesAtual);

        // Dados mês anterior
        BigDecimal vendasAnterior = pedidoRepository.sumTotalByEmpresaIdAndPeriodo(empresa.getId(), inicioMesAnterior, fimMesAnterior);
        Long pedidosAnterior = pedidoRepository.countByEmpresaIdAndPeriodo(empresa.getId(), inicioMesAnterior, fimMesAnterior);

        comparativo.put("mesAtual", Map.of(
                "vendas", vendasAtual != null ? vendasAtual : BigDecimal.ZERO,
                "pedidos", pedidosAtual,
                "periodo", mesAtual + "/" + anoAtual
        ));

        comparativo.put("mesAnterior", Map.of(
                "vendas", vendasAnterior != null ? vendasAnterior : BigDecimal.ZERO,
                "pedidos", pedidosAnterior,
                "periodo", mesAnteriorYM.getMonthValue() + "/" + mesAnteriorYM.getYear()
        ));

        // Variações percentuais
        if (vendasAnterior != null && vendasAnterior.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal variacaoVendas = vendasAtual != null ?
                    vendasAtual.subtract(vendasAnterior).divide(vendasAnterior, 4, java.math.RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) :
                    BigDecimal.valueOf(-100);
            comparativo.put("variacaoVendas", variacaoVendas);
        } else {
            comparativo.put("variacaoVendas", BigDecimal.ZERO);
        }

        if (pedidosAnterior > 0) {
            double variacaoPedidos = ((double) (pedidosAtual - pedidosAnterior) / pedidosAnterior) * 100;
            comparativo.put("variacaoPedidos", variacaoPedidos);
        } else {
            comparativo.put("variacaoPedidos", 0.0);
        }

        return comparativo;
    }

    public byte[] exportarRelatorioPDF(String emailEmpresa, int mes, int ano) {
        // Implementação básica - retorna um PDF simples
        // Em uma implementação real, você usaria uma biblioteca como iText ou JasperReports
        RelatorioDTO relatorio = gerarRelatorioMensal(emailEmpresa, mes, ano);

        String conteudo = String.format(
                "RELATÓRIO MENSAL\n\n" +
                        "Período: %02d/%d\n" +
                        "Faturamento: R$ %.2f\n" +
                        "Pedidos: %d\n" +
                        "Ticket Médio: R$ %.2f\n" +
                        "Avaliação Média: %.1f\n",
                mes, ano,
                relatorio.getFaturamentoMensal(),
                relatorio.getQuantidadePedidosMensal(),
                relatorio.getTicketMedio(),
                relatorio.getAvaliacaoMedia()
        );

        return conteudo.getBytes();
    }

    private Empresa buscarEmpresaPorEmail(String email) {
        return empresaRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Empresa não encontrada"));
    }

    private RelatorioDTO criarRelatorio(Empresa empresa, LocalDateTime inicio, LocalDateTime fim, boolean isAnual) {
        RelatorioDTO relatorio = new RelatorioDTO();

        // Faturamento do período
        BigDecimal faturamentoPeriodo = pedidoRepository.sumTotalByEmpresaIdAndPeriodo(empresa.getId(), inicio, fim);

        if (isAnual) {
            relatorio.setFaturamentoAnual(faturamentoPeriodo != null ? faturamentoPeriodo : BigDecimal.ZERO);
            relatorio.setFaturamentoMensal(BigDecimal.ZERO);
        } else {
            relatorio.setFaturamentoMensal(faturamentoPeriodo != null ? faturamentoPeriodo : BigDecimal.ZERO);

            // Para relatório mensal, também calcular o anual
            LocalDateTime inicioAno = LocalDateTime.of(inicio.getYear(), 1, 1, 0, 0, 0);
            LocalDateTime fimAno = LocalDateTime.of(inicio.getYear(), 12, 31, 23, 59, 59);
            BigDecimal faturamentoAnual = pedidoRepository.sumTotalByEmpresaIdAndPeriodo(empresa.getId(), inicioAno, fimAno);
            relatorio.setFaturamentoAnual(faturamentoAnual != null ? faturamentoAnual : BigDecimal.ZERO);
        }

        // Quantidade de pedidos
        Long pedidosPeriodo = pedidoRepository.countByEmpresaIdAndPeriodo(empresa.getId(), inicio, fim);

        if (isAnual) {
            relatorio.setQuantidadePedidosAnual(pedidosPeriodo.intValue());
            relatorio.setQuantidadePedidosMensal(0);
        } else {
            relatorio.setQuantidadePedidosMensal(pedidosPeriodo.intValue());

            LocalDateTime inicioAno = LocalDateTime.of(inicio.getYear(), 1, 1, 0, 0, 0);
            LocalDateTime fimAno = LocalDateTime.of(inicio.getYear(), 12, 31, 23, 59, 59);
            Long pedidosAnual = pedidoRepository.countByEmpresaIdAndPeriodo(empresa.getId(), inicioAno, fimAno);
            relatorio.setQuantidadePedidosAnual(pedidosAnual.intValue());
        }

        // Ticket médio
        if (pedidosPeriodo > 0 && faturamentoPeriodo != null) {
            BigDecimal ticketMedio = faturamentoPeriodo.divide(BigDecimal.valueOf(pedidosPeriodo), 2, java.math.RoundingMode.HALF_UP);
            relatorio.setTicketMedio(ticketMedio);
        } else {
            relatorio.setTicketMedio(BigDecimal.ZERO);
        }

        // Avaliações
        Double avaliacaoMedia = feedbackRepository.findAvaliacaoMediaByEmpresaId(empresa.getId());
        relatorio.setAvaliacaoMedia(avaliacaoMedia != null ? avaliacaoMedia : 0.0);

        Long totalAvaliacoes = feedbackRepository.countByEmpresaId(empresa.getId());
        relatorio.setTotalAvaliacoes(totalAvaliacoes.intValue());

        return relatorio;
    }
}