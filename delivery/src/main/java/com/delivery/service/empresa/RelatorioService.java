package com.delivery.service.empresa;

import com.delivery.dto.empresa.RelatorioDTO;
import com.delivery.entity.Empresa;
import com.delivery.entity.Pedido;
import com.delivery.entity.enums.StatusPedido;
import com.delivery.exception.NotFoundException;
import com.delivery.repository.EmpresaRepository;
import com.delivery.repository.FeedbackRepository;
import com.delivery.repository.PedidoRepository;
import com.delivery.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Map<String, Object> gerarDadosDashboard(String emailEmpresa) {
        Empresa empresa = buscarEmpresaPorEmail(emailEmpresa);
        Map<String, Object> dashboard = new HashMap<>();

        // Dados do mês atual
        LocalDate hoje = LocalDate.now();
        YearMonth mesAtual = YearMonth.from(hoje);
        LocalDateTime inicioMes = mesAtual.atDay(1).atStartOfDay();
        LocalDateTime fimMes = mesAtual.atEndOfMonth().atTime(23, 59, 59);

        // Dados do mês anterior
        YearMonth mesAnterior = mesAtual.minusMonths(1);
        LocalDateTime inicioMesAnterior = mesAnterior.atDay(1).atStartOfDay();
        LocalDateTime fimMesAnterior = mesAnterior.atEndOfMonth().atTime(23, 59, 59);

        // Calcular dados do mês atual
        List<Pedido> pedidosMes = pedidoRepository.findByEmpresaIdAndPeriodo(
                empresa.getId(), inicioMes, fimMes);

        BigDecimal faturamentoMes = calcularFaturamento(pedidosMes);
        BigDecimal custoMes = calcularCustos(pedidosMes);
        BigDecimal lucroMes = faturamentoMes.subtract(custoMes);

        // Calcular dados do mês anterior
        List<Pedido> pedidosMesAnterior = pedidoRepository.findByEmpresaIdAndPeriodo(
                empresa.getId(), inicioMesAnterior, fimMesAnterior);

        BigDecimal faturamentoMesAnterior = calcularFaturamento(pedidosMesAnterior);
        BigDecimal lucroMesAnterior = faturamentoMesAnterior.subtract(calcularCustos(pedidosMesAnterior));

        // Calcular dados do ano
        LocalDateTime inicioAno = LocalDateTime.of(hoje.getYear(), 1, 1, 0, 0, 0);
        LocalDateTime fimAno = LocalDateTime.of(hoje.getYear(), 12, 31, 23, 59, 59);

        List<Pedido> pedidosAno = pedidoRepository.findByEmpresaIdAndPeriodo(
                empresa.getId(), inicioAno, fimAno);

        BigDecimal faturamentoAno = calcularFaturamento(pedidosAno);
        BigDecimal custoAno = calcularCustos(pedidosAno);
        BigDecimal lucroAno = faturamentoAno.subtract(custoAno);

        // Métricas principais
        dashboard.put("faturamentoMes", faturamentoMes);
        dashboard.put("lucroMes", lucroMes);
        dashboard.put("pedidosMes", (long) pedidosMes.size());
        dashboard.put("ticketMedio", pedidosMes.size() > 0 ?
                faturamentoMes.divide(BigDecimal.valueOf(pedidosMes.size()), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO);

        dashboard.put("faturamentoAno", faturamentoAno);
        dashboard.put("lucroAno", lucroAno);
        dashboard.put("pedidosAno", (long) pedidosAno.size());

        // Variações percentuais
        BigDecimal variacaoFaturamento = calcularVariacaoPercentual(faturamentoMes, faturamentoMesAnterior);
        BigDecimal variacaoLucro = calcularVariacaoPercentual(lucroMes, lucroMesAnterior);
        double variacaoPedidos = calcularVariacaoPedidos(pedidosMes.size(), pedidosMesAnterior.size());

        dashboard.put("variacaoFaturamento", variacaoFaturamento);
        dashboard.put("variacaoLucro", variacaoLucro);
        dashboard.put("variacaoPedidos", variacaoPedidos);

        // Produtos
        Long totalProdutos = produtoRepository.countByEmpresaId(empresa.getId());
        Long produtosAtivos = produtoRepository.countByEmpresaIdAndAtivoTrue(empresa.getId());
        Long produtosBaixoEstoque = produtoRepository.countByEmpresaIdAndEstoqueLessThan(empresa.getId(), 10);

        dashboard.put("totalProdutos", totalProdutos);
        dashboard.put("produtosAtivos", produtosAtivos);
        dashboard.put("produtosBaixoEstoque", produtosBaixoEstoque);

        // Avaliações
        Double avaliacaoMedia = feedbackRepository.findAvaliacaoMediaByEmpresaId(empresa.getId());
        Long totalAvaliacoes = feedbackRepository.countByEmpresaId(empresa.getId());

        dashboard.put("avaliacaoMedia", avaliacaoMedia != null ? avaliacaoMedia : 0.0);
        dashboard.put("totalAvaliacoes", totalAvaliacoes);

        // Margem de lucro
        BigDecimal margemLucroMes = faturamentoMes.compareTo(BigDecimal.ZERO) > 0 ?
                lucroMes.divide(faturamentoMes, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) :
                BigDecimal.ZERO;
        BigDecimal margemLucroAno = faturamentoAno.compareTo(BigDecimal.ZERO) > 0 ?
                lucroAno.divide(faturamentoAno, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) :
                BigDecimal.ZERO;

        dashboard.put("margemLucroMes", margemLucroMes);
        dashboard.put("margemLucroAno", margemLucroAno);

        return dashboard;
    }

    public Map<String, Object> gerarRelatorioDetalhado(String emailEmpresa, int mes, int ano) {
        Empresa empresa = buscarEmpresaPorEmail(emailEmpresa);
        Map<String, Object> relatorio = new HashMap<>();

        YearMonth yearMonth = YearMonth.of(ano, mes);
        LocalDateTime inicioMes = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime fimMes = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Pedido> pedidos = pedidoRepository.findByEmpresaIdAndPeriodo(
                empresa.getId(), inicioMes, fimMes);

        // Análise financeira detalhada
        BigDecimal faturamentoBruto = calcularFaturamento(pedidos);
        BigDecimal custoTotal = calcularCustos(pedidos);
        BigDecimal lucroLiquido = faturamentoBruto.subtract(custoTotal);

        relatorio.put("periodo", mes + "/" + ano);
        relatorio.put("faturamentoBruto", faturamentoBruto);
        relatorio.put("custoTotal", custoTotal);
        relatorio.put("lucroLiquido", lucroLiquido);
        relatorio.put("totalPedidos", pedidos.size());

        // Margem de lucro
        BigDecimal margemLucro = faturamentoBruto.compareTo(BigDecimal.ZERO) > 0 ?
                lucroLiquido.divide(faturamentoBruto, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) :
                BigDecimal.ZERO;
        relatorio.put("margemLucro", margemLucro);

        // Análise por status de pedido
        Map<String, Object> pedidosPorStatus = pedidos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getStatus().name(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                lista -> Map.of(
                                        "quantidade", lista.size(),
                                        "valor", calcularFaturamento(lista)
                                )
                        )
                ));
        relatorio.put("pedidosPorStatus", pedidosPorStatus);

        // Produtos mais vendidos
        Map<String, Object> produtosMaisVendidos = obterProdutosMaisVendidos(pedidos);
        relatorio.put("produtosMaisVendidos", produtosMaisVendidos);

        // Clientes mais frequentes
        Map<String, Object> clientesFrequentes = obterClientesFrequentes(pedidos);
        relatorio.put("clientesFrequentes", clientesFrequentes);

        // Análise temporal (vendas por dia)
        Map<String, Object> vendasPorDia = obterVendasPorDia(pedidos);
        relatorio.put("vendasPorDia", vendasPorDia);

        return relatorio;
    }

    private RelatorioDTO criarRelatorio(Empresa empresa, LocalDateTime inicio, LocalDateTime fim, boolean isAnual) {
        RelatorioDTO relatorio = new RelatorioDTO();

        // Buscar pedidos do período
        List<Pedido> pedidos = pedidoRepository.findByEmpresaIdAndPeriodo(empresa.getId(), inicio, fim);

        // Calcular faturamento e custos
        BigDecimal faturamento = calcularFaturamento(pedidos);
        BigDecimal custos = calcularCustos(pedidos);
        BigDecimal lucro = faturamento.subtract(custos);

        if (isAnual) {
            relatorio.setFaturamentoAnual(faturamento);
            relatorio.setQuantidadePedidosAnual(pedidos.size());
            // Adicionar campo de lucro anual no DTO se necessário
        } else {
            relatorio.setFaturamentoMensal(faturamento);
            relatorio.setQuantidadePedidosMensal(pedidos.size());

            // Para relatório mensal, calcular também o anual
            LocalDateTime inicioAno = LocalDateTime.of(inicio.getYear(), 1, 1, 0, 0, 0);
            LocalDateTime fimAno = LocalDateTime.of(inicio.getYear(), 12, 31, 23, 59, 59);

            List<Pedido> pedidosAno = pedidoRepository.findByEmpresaIdAndPeriodo(empresa.getId(), inicioAno, fimAno);
            BigDecimal faturamentoAno = calcularFaturamento(pedidosAno);

            relatorio.setFaturamentoAnual(faturamentoAno);
            relatorio.setQuantidadePedidosAnual(pedidosAno.size());
        }

        // Ticket médio
        if (pedidos.size() > 0) {
            BigDecimal ticketMedio = faturamento.divide(BigDecimal.valueOf(pedidos.size()), 2, RoundingMode.HALF_UP);
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

    private BigDecimal calcularFaturamento(List<Pedido> pedidos) {
        return pedidos.stream()
                .filter(p -> p.getStatus() != StatusPedido.CANCELADO)
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularCustos(List<Pedido> pedidos) {
        // Simular cálculo de custos (70% do faturamento para exemplo)
        // Em um sistema real, você teria custos reais dos produtos
        BigDecimal faturamento = calcularFaturamento(pedidos);
        return faturamento.multiply(BigDecimal.valueOf(0.70));
    }

    private BigDecimal calcularVariacaoPercentual(BigDecimal valorAtual, BigDecimal valorAnterior) {
        if (valorAnterior.compareTo(BigDecimal.ZERO) == 0) {
            return valorAtual.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }

        return valorAtual.subtract(valorAnterior)
                .divide(valorAnterior, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private double calcularVariacaoPedidos(int pedidosAtual, int pedidosAnterior) {
        if (pedidosAnterior == 0) {
            return pedidosAtual > 0 ? 100.0 : 0.0;
        }

        return ((double) (pedidosAtual - pedidosAnterior) / pedidosAnterior) * 100;
    }

    private Map<String, Object> obterProdutosMaisVendidos(List<Pedido> pedidos) {
        // Implementar lógica para produtos mais vendidos
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("produtos", List.of());
        return resultado;
    }

    private Map<String, Object> obterClientesFrequentes(List<Pedido> pedidos) {
        // Implementar lógica para clientes mais frequentes
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("clientes", List.of());
        return resultado;
    }

    private Map<String, Object> obterVendasPorDia(List<Pedido> pedidos) {
        // Implementar lógica para vendas por dia
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("vendas", List.of());
        return resultado;
    }

    private Empresa buscarEmpresaPorEmail(String email) {
        return empresaRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Empresa não encontrada"));
    }
}