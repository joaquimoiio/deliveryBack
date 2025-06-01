package com.delivery.dto.auth;

import com.delivery.entity.enums.TipoUsuario;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter um formato válido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    private String senha;

    @NotNull(message = "Tipo de usuário é obrigatório")
    private TipoUsuario tipoUsuario;

    // Campos para cliente
    private String nome;
    private String cpf;
    private String telefoneCliente;
    private String enderecoCliente;

    // Campos para empresa
    private String nomeFantasia;
    private String cnpj;
    private String telefoneEmpresa;
    private String enderecoEmpresa;
    private Long categoriaId;
    private String descricao;
}