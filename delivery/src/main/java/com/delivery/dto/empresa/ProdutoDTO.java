package com.delivery.dto.empresa;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProdutoDTO {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String imagemUrl;
    private Integer estoque;
    private Boolean ativo;
    private CategoriaDTO categoria;
    private Long empresaId;
}