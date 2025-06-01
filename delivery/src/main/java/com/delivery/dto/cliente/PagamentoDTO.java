package com.delivery.dto.cliente;

import com.delivery.entity.enums.FormaPagamento;
import lombok.Data;

@Data
public class PagamentoDTO {
    private Long pedidoId;
    private FormaPagamento formaPagamento;
    private String numeroCartao; // apenas últimos 4 dígitos
    private String nomePortador;
    private String codigoPix;
    private Double valor;
}