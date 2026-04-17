package com.plagicheck.repository;

import com.plagicheck.entity.Soutenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SoutenanceRepository extends JpaRepository<Soutenance, Long> {
    Optional<Soutenance> findByThemeId(Long themeId);
    List<Soutenance> findByEtudiantId(Long etudiantId);
    List<Soutenance> findByDate(LocalDate date);
}
