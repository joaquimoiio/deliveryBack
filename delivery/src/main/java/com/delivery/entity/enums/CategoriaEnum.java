package com.delivery.entity.enums;

public enum CategoriaEnum {
    RESTAURANTE("Restaurante", "restaurante", "🍽️"),
    LANCHONETE("Lanchonete", "lanchonete", "🍔"),
    PIZZARIA("Pizzaria", "pizzaria", "🍕"),
    HAMBURGUERIA("Hamburgueria", "hamburgueria", "🍔"),
    JAPONESA("Comida Japonesa", "comida-japonesa", "🍣"),
    ITALIANA("Comida Italiana", "italiana", "🍝"),
    CHINESA("Comida Chinesa", "chinesa", "🥢"),
    BRASILEIRA("Comida Brasileira", "brasileira", "🍖"),
    MEXICANA("Comida Mexicana", "mexicana", "🌮"),
    INDIANA("Comida Indiana", "indiana", "🍛"),
    ARABE("Comida Árabe", "arabe", "🥙"),
    VEGETARIANA("Vegetariana", "vegetariana", "🥗"),
    VEGANA("Vegana", "vegana", "🌱"),
    DOCES("Doces e Sobremesas", "doces", "🍰"),
    SORVETE("Sorveteria", "sorvete", "🍦"),
    ACAI("Açaí", "acai", "🍇"),
    SUCO("Sucos e Vitaminas", "suco", "🥤"),
    CAFE("Cafeteria", "cafe", "☕"),
    PADARIA("Padaria", "padaria", "🥖"),
    FARMACIA("Farmácia", "farmacia", "💊"),
    MERCADO("Mercado", "mercado", "🛒"),
    PET_SHOP("Pet Shop", "pet-shop", "🐕"),
    BEBIDAS("Bebidas", "bebidas", "🍻"),
    CONVENIENCIA("Conveniência", "conveniencia", "🏪"),
    FLORES("Floricultura", "flores", "🌹"),
    PRESENTE("Presentes", "presente", "🎁");

    private final String nome;
    private final String slug;
    private final String icone;

    CategoriaEnum(String nome, String slug, String icone) {
        this.nome = nome;
        this.slug = slug;
        this.icone = icone;
    }

    public String getNome() {
        return nome;
    }

    public String getSlug() {
        return slug;
    }

    public String getIcone() {
        return icone;
    }

    public static CategoriaEnum fromSlug(String slug) {
        for (CategoriaEnum categoria : values()) {
            if (categoria.getSlug().equals(slug)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Categoria não encontrada para o slug: " + slug);
    }

    public static CategoriaEnum fromNome(String nome) {
        for (CategoriaEnum categoria : values()) {
            if (categoria.getNome().equalsIgnoreCase(nome)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Categoria não encontrada para o nome: " + nome);
    }
}