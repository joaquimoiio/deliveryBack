package com.delivery.dto.empresa;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RelatorioDTO {
    private BigDecimal faturamentoMensal;
    private BigDecimal faturamentoAnual;
    private Integer quantidadePedidosMensal;
    private Integer quantidadePedidosAnual;
    private BigDecimal ticketMedio;
    private List<ProdutoMaisVendidoDTO> produtosMaisVendidos;
    private Double avaliacaoMedia;
    private Integer totalAvaliacoes;

    @Data
    public static class ProdutoMaisVendidoDTO {
        private String nomeProduto;
        private Integer quantidadeVendida;
        private BigDecimal faturamento;
    }
}