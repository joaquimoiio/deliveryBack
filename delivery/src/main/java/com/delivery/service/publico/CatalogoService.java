package com.delivery.service.publico;

import com.delivery.dto.publico.CategoriaDTO;
import com.delivery.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaDTO> listarCategorias() {
        return categoriaRepository.findAllAtivasOrderByNome()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    private CategoriaDTO convertToDTO(com.delivery.entity.Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNome(categoria.getNome());
        dto.setSlug(categoria.getSlug());
        dto.setIcone(categoria.getIcone());
        dto.setDescricao(categoria.getDescricao());
        return dto;
    }
}
