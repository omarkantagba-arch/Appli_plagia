package com.plagicheck.repository;

import com.plagicheck.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {
    List<Theme> findByEtudiantId(Long etudiantId);
    List<Theme> findByFiliere(String filiere);
    List<Theme> findByStatut(Theme.Statut statut);
    List<Theme> findByFiliereAndStatut(String filiere, Theme.Statut statut);
    
    @Query("SELECT t FROM Theme t WHERE t.statut = 'VALIDE' AND t.id NOT IN (SELECT s.theme.id FROM Soutenance s)")
    List<Theme> findValidatedThemesWithoutSoutenance();
}
