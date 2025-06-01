package com.delivery.repository;

import com.delivery.entity.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    @Query("SELECT e FROM Empresa e WHERE e.usuario.email = :email")
    Optional<Empresa> findByEmail(@Param("email") String email);

    boolean existsByCnpj(String cnpj);

    @Query("SELECT e FROM Empresa e WHERE e.usuario.id = :usuarioId")
    Optional<Empresa> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT e FROM Empresa e WHERE e.ativo = true")
    Page<Empresa> findAllAtivas(Pageable pageable);

    @Query("SELECT e FROM Empresa e WHERE e.categoria.id = :categoriaId AND e.ativo = true")
    Page<Empresa> findByCategoriaId(@Param("categoriaId") Long categoriaId, Pageable pageable);

    @Query("SELECT e FROM Empresa e WHERE " +
            "(:termo IS NULL OR LOWER(e.nomeFantasia) LIKE LOWER(CONCAT('%', :termo, '%'))) AND " +
            "(:categoriaId IS NULL OR e.categoria.id = :categoriaId) AND " +
            "e.ativo = true")
    Page<Empresa> findByTermoAndCategoria(@Param("termo") String termo,
                                          @Param("categoriaId") Long categoriaId,
                                          Pageable pageable);

    @Query(value = "SELECT e.*, " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(e.latitude)) * " +
            "cos(radians(e.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(e.latitude)))) AS distancia " +
            "FROM empresas e " +
            "WHERE e.ativo = true " +
            "HAVING distancia <= :raioKm " +
            "ORDER BY distancia",
            nativeQuery = true)
    List<Empresa> findByLocalizacao(@Param("latitude") Double latitude,
                                    @Param("longitude") Double longitude,
                                    @Param("raioKm") Double raioKm);

    @Query("SELECT COUNT(e) FROM Empresa e WHERE e.ativo = true")
    Long countByAtivoTrue();

    @Query("SELECT COUNT(e) FROM Empresa e WHERE e.categoria.id = :categoriaId")
    Long countByCategoriaId(@Param("categoriaId") Long categoriaId);
}