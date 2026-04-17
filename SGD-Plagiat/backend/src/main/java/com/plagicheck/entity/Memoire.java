package com.plagicheck.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "memoires")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Memoire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private String auteur;

    @Column(nullable = false)
    private String filiere;

    @Column(nullable = false)
    private Integer annee;

    @Column(nullable = false)
    private String fichierUrl;

    @Column(columnDefinition = "TEXT[]")
    private String[] motsCles;

    @Column(columnDefinition = "TEXT")
    private String resume;

    @OneToOne
    @JoinColumn(name = "rapport_id")
    private Rapport rapport;
}
