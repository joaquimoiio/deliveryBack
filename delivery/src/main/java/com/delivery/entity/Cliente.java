package com.delivery.entity;

import com.delivery.entity.base.BaseEntity;
import com.delivery.entity.base.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends BaseEntity {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String cpf;

    private String telefone;

    @Column(length = 500)
    private String endereco;

    private Double latitude;
    private Double longitude;
}