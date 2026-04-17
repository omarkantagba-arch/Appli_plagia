package com.plagicheck.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "themes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String filiere;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statut statut = Statut.EN_ATTENTE;

    private Double plagiatScore;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private User etudiant;

    @ManyToOne
    @JoinColumn(name = "coordinateur_id")
    private User coordinateur;

    @ManyToOne
    @JoinColumn(name = "validateur_id")
    private User validateur;

    @ManyToOne
    @JoinColumn(name = "encadrant_id")
    private User encadrant;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum Statut {
        EN_ATTENTE, PROPOSE_VALIDATION, PROPOSE_REJET, VALIDE, REJETE
    }
}
