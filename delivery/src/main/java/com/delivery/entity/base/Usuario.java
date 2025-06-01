package com.delivery.entity.base;

import com.delivery.entity.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    private TipoUsuario tipoUsuario;

    @Column(nullable = false)
    private Boolean ativo = true;
}