package com.plagicheck.repository;

import com.plagicheck.entity.Rapport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RapportRepository extends JpaRepository<Rapport, Long> {
    List<Rapport> findByThemeId(Long themeId);
    List<Rapport> findByEtudiantId(Long etudiantId);
    List<Rapport> findByStatut(Rapport.Statut statut);
}
