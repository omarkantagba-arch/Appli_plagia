package com.plagicheck.repository;

import com.plagicheck.entity.Memoire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoireRepository extends JpaRepository<Memoire, Long> {
    List<Memoire> findByFiliere(String filiere);
    List<Memoire> findByAnnee(Integer annee);
    List<Memoire> findByFiliereAndAnnee(String filiere, Integer annee);
}
