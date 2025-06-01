package com.delivery.entity;

import com.delivery.entity.base.BaseEntity;
import com.delivery.entity.enums.StatusPedido;
import com.delivery.entity.enums.FormaPagamento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false)
    private FormaPagamento formaPagamento;

    @Column(length = 500)
    private String observacoes;

    @Column(name = "endereco_entrega", length = 500)
    private String enderecoEntrega;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itens;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Feedback feedback;
}