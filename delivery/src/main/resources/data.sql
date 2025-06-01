-- Criar arquivo src/main/resources/data.sql

-- Inserir categorias básicas
INSERT INTO categorias (nome, slug, icone, descricao, ativo, created_at, updated_at) VALUES
('Restaurante', 'restaurante', '🍽️', 'Categoria Restaurante', true, NOW(), NOW()),
('Lanchonete', 'lanchonete', '🍔', 'Categoria Lanchonete', true, NOW(), NOW()),
('Pizzaria', 'pizzaria', '🍕', 'Categoria Pizzaria', true, NOW(), NOW()),
('Hamburgueria', 'hamburgueria', '🍔', 'Categoria Hamburgueria', true, NOW(), NOW()),
('Comida Japonesa', 'japonesa', '🍣', 'Categoria Comida Japonesa', true, NOW(), NOW()),
('Comida Italiana', 'italiana', '🍝', 'Categoria Comida Italiana', true, NOW(), NOW()),
('Comida Brasileira', 'brasileira', '🍖', 'Categoria Comida Brasileira', true, NOW(), NOW()),
('Doces e Sobremesas', 'doces', '🍰', 'Categoria Doces e Sobremesas', true, NOW(), NOW()),
('Sorveteria', 'sorvete', '🍦', 'Categoria Sorveteria', true, NOW(), NOW()),
('Cafeteria', 'cafe', '☕', 'Categoria Cafeteria', true, NOW(), NOW()),
('Padaria', 'padaria', '🥖', 'Categoria Padaria', true, NOW(), NOW());