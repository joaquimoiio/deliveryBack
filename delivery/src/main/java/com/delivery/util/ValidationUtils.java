package com.delivery.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationUtils {

    private ValidationUtils() {
        // Utility class
    }

    // Padrões de regex
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern CPF_PATTERN = Pattern.compile("\\d{11}");
    private static final Pattern CNPJ_PATTERN = Pattern.compile("\\d{14}");
    private static final Pattern TELEFONE_PATTERN = Pattern.compile("\\d{10,11}");
    private static final Pattern CEP_PATTERN = Pattern.compile("\\d{8}");

    /**
     * Valida email
     */
    public static boolean isEmailValido(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Valida CPF
     */
    public static boolean isCpfValido(String cpf) {
        if (cpf == null || !CPF_PATTERN.matcher(cpf).matches()) {
            return false;
        }

        // Remove formatação se houver
        cpf = cpf.replaceAll("[^\\d]", "");

        // Verifica se não são todos os dígitos iguais
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Valida dígitos verificadores
        return validarDigitoVerificadorCpf(cpf);
    }

    /**
     * Valida CNPJ
     */
    public static boolean isCnpjValido(String cnpj) {
        if (cnpj == null || !CNPJ_PATTERN.matcher(cnpj).matches()) {
            return false;
        }

        // Remove formatação se houver
        cnpj = cnpj.replaceAll("[^\\d]", "");

        // Verifica se não são todos os dígitos iguais
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        // Valida dígitos verificadores
        return validarDigitoVerificadorCnpj(cnpj);
    }

    /**
     * Valida telefone
     */
    public static boolean isTelefoneValido(String telefone) {
        if (telefone == null) {
            return false;
        }

        // Remove formatação
        String telefoneNumerico = telefone.replaceAll("[^\\d]", "");
        return TELEFONE_PATTERN.matcher(telefoneNumerico).matches();
    }

    /**
     * Valida CEP
     */
    public static boolean isCepValido(String cep) {
        if (cep == null) {
            return false;
        }

        // Remove formatação
        String cepNumerico = cep.replaceAll("[^\\d]", "");
        return CEP_PATTERN.matcher(cepNumerico).matches();
    }

    /**
     * Valida string não vazia
     */
    public static boolean isStringValida(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Valida string com tamanho mínimo
     */
    public static boolean isStringValidaComTamanhoMinimo(String str, int tamanhoMinimo) {
        return isStringValida(str) && str.trim().length() >= tamanhoMinimo;
    }

    /**
     * Valida string com tamanho máximo
     */
    public static boolean isStringValidaComTamanhoMaximo(String str, int tamanhoMaximo) {
        return str != null && str.length() <= tamanhoMaximo;
    }

    /**
     * Valida valor monetário
     */
    public static boolean isValorMonetarioValido(BigDecimal valor) {
        return valor != null && valor.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Valida valor monetário positivo
     */
    public static boolean isValorMonetarioPositivo(BigDecimal valor) {
        return valor != null && valor.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Valida coordenadas de latitude
     */
    public static boolean isLatitudeValida(Double latitude) {
        return latitude != null && latitude >= -90.0 && latitude <= 90.0;
    }

    /**
     * Valida coordenadas de longitude
     */
    public static boolean isLongitudeValida(Double longitude) {
        return longitude != null && longitude >= -180.0 && longitude <= 180.0;
    }

    /**
     * Valida nota de avaliação (1 a 5)
     */
    public static boolean isNotaValida(Integer nota) {
        return nota != null && nota >= 1 && nota <= 5;
    }

    /**
     * Valida quantidade
     */
    public static boolean isQuantidadeValida(Integer quantidade) {
        return quantidade != null && quantidade > 0;
    }

    /**
     * Valida senha
     */
    public static boolean isSenhaValida(String senha) {
        return isStringValidaComTamanhoMinimo(senha, 6);
    }

    /**
     * Valida senha forte
     */
    public static boolean isSenhaForte(String senha) {
        if (!isStringValidaComTamanhoMinimo(senha, 8)) {
            return false;
        }

        // Verifica se contém ao menos uma letra minúscula, uma maiúscula, um número e um caractere especial
        boolean temMinuscula = senha.matches(".*[a-z].*");
        boolean temMaiuscula = senha.matches(".*[A-Z].*");
        boolean temNumero = senha.matches(".*\\d.*");
        boolean temEspecial = senha.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        return temMinuscula && temMaiuscula && temNumero && temEspecial;
    }

    /**
     * Formata CPF
     */
    public static String formatarCpf(String cpf) {
        if (cpf == null) return null;

        String cpfNumerico = cpf.replaceAll("[^\\d]", "");
        if (cpfNumerico.length() != 11) return cpf;

        return cpfNumerico.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    /**
     * Formata CNPJ
     */
    public static String formatarCnpj(String cnpj) {
        if (cnpj == null) return null;

        String cnpjNumerico = cnpj.replaceAll("[^\\d]", "");
        if (cnpjNumerico.length() != 14) return cnpj;

        return cnpjNumerico.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }

    /**
     * Formata telefone
     */
    public static String formatarTelefone(String telefone) {
        if (telefone == null) return null;

        String telefoneNumerico = telefone.replaceAll("[^\\d]", "");

        if (telefoneNumerico.length() == 10) {
            return telefoneNumerico.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        } else if (telefoneNumerico.length() == 11) {
            return telefoneNumerico.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        }

        return telefone;
    }

    /**
     * Formata CEP
     */
    public static String formatarCep(String cep) {
        if (cep == null) return null;

        String cepNumerico = cep.replaceAll("[^\\d]", "");
        if (cepNumerico.length() != 8) return cep;

        return cepNumerico.replaceAll("(\\d{5})(\\d{3})", "$1-$2");
    }

    /**
     * Remove formatação de string
     */
    public static String removerFormatacao(String str) {
        return str != null ? str.replaceAll("[^\\d]", "") : null;
    }

    // Métodos privados para validação de dígitos verificadores

    private static boolean validarDigitoVerificadorCpf(String cpf) {
        // Cálculo do primeiro dígito verificador
        int soma1 = 0;
        for (int i = 0; i < 9; i++) {
            soma1 += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int digito1 = 11 - (soma1 % 11);
        if (digito1 >= 10) digito1 = 0;

        // Cálculo do segundo dígito verificador
        int soma2 = 0;
        for (int i = 0; i < 10; i++) {
            soma2 += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int digito2 = 11 - (soma2 % 11);
        if (digito2 >= 10) digito2 = 0;

        // Verifica os dígitos
        return Character.getNumericValue(cpf.charAt(9)) == digito1 &&
                Character.getNumericValue(cpf.charAt(10)) == digito2;
    }

    private static boolean validarDigitoVerificadorCnpj(String cnpj) {
        // Pesos para o primeiro dígito
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        // Cálculo do primeiro dígito verificador
        int soma1 = 0;
        for (int i = 0; i < 12; i++) {
            soma1 += Character.getNumericValue(cnpj.charAt(i)) * pesos1[i];
        }
        int digito1 = 11 - (soma1 % 11);
        if (digito1 >= 10) digito1 = 0;

        // Pesos para o segundo dígito
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        // Cálculo do segundo dígito verificador
        int soma2 = 0;
        for (int i = 0; i < 13; i++) {
            soma2 += Character.getNumericValue(cnpj.charAt(i)) * pesos2[i];
        }
        int digito2 = 11 - (soma2 % 11);
        if (digito2 >= 10) digito2 = 0;

        // Verifica os dígitos
        return Character.getNumericValue(cnpj.charAt(12)) == digito1 &&
                Character.getNumericValue(cnpj.charAt(13)) == digito2;
    }
}