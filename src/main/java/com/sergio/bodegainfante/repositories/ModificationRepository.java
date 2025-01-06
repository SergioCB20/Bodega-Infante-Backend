package com.sergio.bodegainfante.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.sergio.bodegainfante.models.Modification;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ModificationRepository extends JpaRepository<Modification, Long> {

    // Filtrar modificaciones por fechas y por adminId
    @Query("SELECT m FROM Modification m WHERE " +
            "(m.created_at BETWEEN :startDate AND :endDate) " +
            "AND (:adminId IS NULL OR m.admin.user_id = :adminId)")
    List<Modification> findFilteredModifications(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("adminId") Long adminId);
}

