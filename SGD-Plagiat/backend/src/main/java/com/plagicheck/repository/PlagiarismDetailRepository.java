package com.plagicheck.repository;

import com.plagicheck.entity.PlagiarismDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlagiarismDetailRepository extends JpaRepository<PlagiarismDetail, Long> {
    Optional<PlagiarismDetail> findByRapportId(Long rapportId);
}
