package com.delivery.repository;

import com.delivery.entity.Pedido;
import com.delivery.entity.enums.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId ORDER BY p.createdAt DESC")
    Page<Pedido> findByClienteId(@Param("clienteId") Long clienteId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.empresa.id = :empresaId ORDER BY p.createdAt DESC")
    Page<Pedido> findByEmpresaId(@Param("empresaId") Long empresaId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.empresa.id = :empresaId AND p.status = :status")
    List<Pedido> findByEmpresaIdAndStatus(@Param("empresaId") Long empresaId,
                                          @Param("status") StatusPedido status);

    @Query("SELECT p FROM Pedido p WHERE p.empresa.id = :empresaId AND " +
            "p.createdAt BETWEEN :inicio AND :fim")
    List<Pedido> findByEmpresaIdAndPeriodo(@Param("empresaId") Long empresaId,
                                           @Param("inicio") LocalDateTime inicio,
                                           @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.empresa.id = :empresaId AND " +
            "p.createdAt BETWEEN :inicio AND :fim")
    Long countByEmpresaIdAndPeriodo(@Param("empresaId") Long empresaId,
                                    @Param("inicio") LocalDateTime inicio,
                                    @Param("fim") LocalDateTime fim);

    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.empresa.id = :empresaId AND " +
            "p.createdAt BETWEEN :inicio AND :fim AND p.status != 'CANCELADO'")
    java.math.BigDecimal sumTotalByEmpresaIdAndPeriodo(@Param("empresaId") Long empresaId,
                                                       @Param("inicio") LocalDateTime inicio,
                                                       @Param("fim") LocalDateTime fim);

    @Query("SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId AND p.status = :status ORDER BY p.createdAt DESC")
    Page<Pedido> findByClienteIdAndStatus(@Param("clienteId") Long clienteId, @Param("status") StatusPedido status, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.cliente.id = :clienteId")
    Long countByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.cliente.id = :clienteId AND p.status != 'CANCELADO'")
    BigDecimal sumTotalByClienteId(@Param("clienteId") Long clienteId);

}