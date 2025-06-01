package com.delivery.dto.publico;

import lombok.Data;

@Data
public class CategoriaDTO {
    private Long id;
    private String nome;
    private String slug;
    private String icone;
    private String descricao;
}