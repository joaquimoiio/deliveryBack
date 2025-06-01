package com.delivery.config;

import com.delivery.entity.Categoria;
import com.delivery.entity.enums.CategoriaEnum;
import com.delivery.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

// @Component  // ← COMENTADO TEMPORARIAMENTE
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            log.info("Iniciando carregamento de dados...");
            carregarCategorias();
            log.info("Carregamento de dados concluído com sucesso!");
        } catch (Exception e) {
            log.error("Erro durante o carregamento de dados: ", e);
        }
    }

    private void carregarCategorias() {
        try {
            long count = categoriaRepository.count();
            log.info("Categorias existentes no banco: {}", count);

            if (count > 0) {
                log.info("Categorias já carregadas, pulando inicialização");
                return;
            }

            log.info("Carregando categorias básicas...");

            List<Categoria> categoriasParaSalvar = new ArrayList<>();

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
                try {
                    Categoria categoria = new Categoria();
                    categoria.setNome(categoriaEnum.getNome());
                    categoria.setSlug(categoriaEnum.getSlug());
                    categoria.setIcone(categoriaEnum.getIcone());
                    categoria.setDescricao("Categoria " + categoriaEnum.getNome());
                    categoria.setAtivo(true);

                    categoriasParaSalvar.add(categoria);

                } catch (Exception e) {
                    log.warn("Erro ao preparar categoria {}: {}", categoriaEnum.getNome(), e.getMessage());
                }
            }

            List<Categoria> categoriasSalvas = categoriaRepository.saveAll(categoriasParaSalvar);
            log.info("Carregadas {} categorias com sucesso", categoriasSalvas.size());

        } catch (Exception e) {
            log.error("Erro ao carregar categorias: ", e);
        }
    }
}