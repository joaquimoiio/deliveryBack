package com.delivery.entity;

import com.delivery.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Categoria extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false, unique = true)
    private String slug;

    private String icone;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "categoria")
    private List<Empresa> empresas;

    @OneToMany(mappedBy = "categoria")
    private List<Produto> produtos;
}
