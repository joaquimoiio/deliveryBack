package com.delivery.repository;

import com.delivery.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("SELECT c FROM Cliente c WHERE c.usuario.email = :email")
    Optional<Cliente> findByEmail(@Param("email") String email);

    boolean existsByCpf(String cpf);

    @Query("SELECT c FROM Cliente c WHERE c.usuario.id = :usuarioId")
    Optional<Cliente> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}