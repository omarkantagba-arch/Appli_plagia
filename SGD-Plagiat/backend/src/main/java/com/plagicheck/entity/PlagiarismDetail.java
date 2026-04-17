package com.plagicheck.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "plagiarism_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlagiarismDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "rapport_id", nullable = false)
    private Rapport rapport;

    @Column(nullable = false)
    private Double localScore;

    @Column(nullable = false)
    private Double webScore;

    @Column(nullable = false)
    private Double finalScore;

    @Column(columnDefinition = "TEXT")
    private String localSources;

    @Column(columnDefinition = "TEXT")
    private String webSources;

    @Column(nullable = false)
    private Boolean webSearchPerformed = false;

    @CreationTimestamp
    private LocalDateTime analyzedAt;
}
