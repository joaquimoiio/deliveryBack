package com.delivery.dto.empresa;

import com.delivery.dto.publico.CategoriaDTO;
import lombok.Data;
import java.util.List;

@Data
public class EmpresaDTO {
    private Long id;
    private String nomeFantasia;
    private String email;
    private String cnpj;
    private String telefone;
    private String endereco;
    private Double latitude;
    private Double longitude;
    private String logoUrl;
    private String descricao;
    private Boolean ativo;
    private CategoriaDTO categoria;
    private List<ProdutoDTO> produtos;
    private Double avaliacao;
    private Integer totalAvaliacoes;
}