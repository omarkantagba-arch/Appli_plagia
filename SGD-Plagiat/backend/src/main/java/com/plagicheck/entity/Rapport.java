package com.plagicheck.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rapports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rapport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fichierUrl;

    @Column(columnDefinition = "TEXT")
    private String textContent;

    private Double plagiatScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statut statut = Statut.EN_ANALYSE;

    @Column(nullable = false)
    private Integer version = 1;

    @ManyToOne
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private User etudiant;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum Statut {
        EN_ANALYSE, VALIDE, REJETE, EN_EXAMEN
    }
}
