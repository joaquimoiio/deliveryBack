package com.delivery.dto.auth;

import com.delivery.entity.enums.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {
    private String token;
    private TipoUsuario tipoUsuario;
    private Long userId;
    private String email;
}
