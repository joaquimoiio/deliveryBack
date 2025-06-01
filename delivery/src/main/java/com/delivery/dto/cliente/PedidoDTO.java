package com.delivery.dto.cliente;

import com.delivery.entity.enums.StatusPedido;
import com.delivery.entity.enums.FormaPagamento;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoDTO {
    private Long id;
    private Long empresaId;
    private String nomeEmpresa;
    private BigDecimal total;
    private StatusPedido status;
    private FormaPagamento formaPagamento;
    private String observacoes;
    private String enderecoEntrega;
    private LocalDateTime dataPedido;
    private List<ItemPedidoDTO> itens;

    @Data
    public static class ItemPedidoDTO {
        private Long produtoId;
        private String nomeProduto;
        private Integer quantidade;
        private BigDecimal precoUnitario;
        private BigDecimal subtotal;
    }
}