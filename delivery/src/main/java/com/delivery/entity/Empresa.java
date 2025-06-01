package com.delivery.entity;

import com.delivery.entity.base.BaseEntity;
import com.delivery.entity.base.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "empresas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Empresa extends BaseEntity {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "nome_fantasia", nullable = false)
    private String nomeFantasia;

    @Column(unique = true, nullable = false)
    private String cnpj;

    private String telefone;

    @Column(length = 500)
    private String endereco;

    private Double latitude;
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(length = 1000)
    private String descricao;

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<Produto> produtos;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;
}