package com.delivery.config;

import com.delivery.entity.Categoria;
import com.delivery.entity.enums.CategoriaEnum;
import com.delivery.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;

    @Override
    public void run(String... args) throws Exception {
        carregarCategorias();
    }

    private void carregarCategorias() {
        // Verifica se já existem categorias no banco
        if (categoriaRepository.count() > 0) {
            return;
        }

        // Carrega algumas categorias básicas
        CategoriaEnum[] categoriasBasicas = {
                CategoriaEnum.RESTAURANTE,
                CategoriaEnum.LANCHONETE,
                CategoriaEnum.PIZZARIA,
                CategoriaEnum.HAMBURGUERIA,
                CategoriaEnum.JAPONESA,
                CategoriaEnum.ITALIANA,
                CategoriaEnum.BRASILEIRA,
                CategoriaEnum.DOCES,
                CategoriaEnum.SORVETE,
                CategoriaEnum.CAFE,
                CategoriaEnum.PADARIA
        };

        for (CategoriaEnum categoriaEnum : categoriasBasicas) {
            if (!categoriaRepository.existsByNome(categoriaEnum.getNome())) {
                Categoria categoria = new Categoria();
                categoria.setNome(categoriaEnum.getNome());
                categoria.setSlug(categoriaEnum.getSlug());
                categoria.setIcone(categoriaEnum.getIcone());
                categoria.setDescricao("Categoria " + categoriaEnum.getNome());
                categoria.setAtivo(true);

                categoriaRepository.save(categoria);
            }
        }
    }
}