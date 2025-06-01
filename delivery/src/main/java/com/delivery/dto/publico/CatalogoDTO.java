package com.delivery.dto.publico;

import com.delivery.dto.empresa.EmpresaDTO;
import com.delivery.dto.empresa.ProdutoDTO;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class CatalogoDTO {
    private List<CategoriaDTO> categorias;
    private Page<EmpresaDTO> empresas;
    private Page<ProdutoDTO> produtos;
    private List<EmpresaDTO> empresasDestaque;
    private List<ProdutoDTO> produtosDestaque;
    private List<EmpresaDTO> empresasPromocao;
    private List<ProdutoDTO> produtosPromocao;
    private EstatisticasCatalogo estatisticas;

    @Data
    public static class EstatisticasCatalogo {
        private Long totalEmpresas;
        private Long totalProdutos;
        private Long totalCategorias;
        private Long empresasAtivas;
        private Long produtosAtivos;
        private Double avaliacaoMediaGeral;
    }
}