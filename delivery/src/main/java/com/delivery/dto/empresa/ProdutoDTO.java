package com.delivery.dto.empresa;

import com.delivery.dto.publico.CategoriaDTO;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProdutoDTO {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String imagemUrl;
    private Boolean ativo;
    private CategoriaDTO categoria;
    private Long categoriaId;
    private Long empresaId;
}
