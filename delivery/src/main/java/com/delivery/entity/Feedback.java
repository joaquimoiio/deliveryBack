package com.delivery.entity;

import com.delivery.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "feedbacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Feedback extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false)
    private Integer nota; // 1 a 5

    @Column(length = 1000)
    private String comentario;
}