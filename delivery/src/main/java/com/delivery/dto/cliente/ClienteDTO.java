package com.delivery.dto.cliente;

import lombok.Data;

@Data
public class ClienteDTO {
    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private String endereco;
    private Double latitude;
    private Double longitude;
}