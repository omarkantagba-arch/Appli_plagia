package com.plagicheck.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "soutenances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Soutenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime heure;

    @Column(nullable = false)
    private String salle;

    @Column(columnDefinition = "TEXT[]")
    private String[] jury;

    @OneToOne
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private User etudiant;

    private Double note;

    @Column(columnDefinition = "TEXT")
    private String appreciation;
}
