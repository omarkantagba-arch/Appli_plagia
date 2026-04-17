-- Script d'initialisation de la base de données PlagiCheck

-- Création de la base de données
-- Note: exécuter cette commande séparément en tant que superuser si nécessaire
-- CREATE DATABASE plagicheck_db;

-- Se connecter à plagicheck_db avant d'exécuter la suite

BEGIN;

-- Table Users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ETUDIANT', 'COORDINATEUR', 'DA')),
    filiere VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table Themes
CREATE TABLE IF NOT EXISTS themes (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(500) NOT NULL,
    description TEXT,
    filiere VARCHAR(100) NOT NULL,
    statut VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE' 
        CHECK (statut IN ('EN_ATTENTE', 'PROPOSE_VALIDATION', 'PROPOSE_REJET', 'VALIDE', 'REJETE')),
    plagiat_score DECIMAL(5,2),
    commentaire TEXT,
    etudiant_id BIGINT NOT NULL REFERENCES users(id),
    coordinateur_id BIGINT REFERENCES users(id),
    validateur_id BIGINT REFERENCES users(id),
    encadrant_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table Rapports
CREATE TABLE IF NOT EXISTS rapports (
    id BIGSERIAL PRIMARY KEY,
    fichier_url VARCHAR(500) NOT NULL,
    text_content TEXT,
    plagiat_score DECIMAL(5,2),
    statut VARCHAR(50) NOT NULL DEFAULT 'EN_ANALYSE' 
        CHECK (statut IN ('EN_ANALYSE', 'VALIDE', 'REJETE', 'EN_EXAMEN')),
    version INTEGER NOT NULL DEFAULT 1,
    theme_id BIGINT NOT NULL REFERENCES themes(id),
    etudiant_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table Soutenances
CREATE TABLE IF NOT EXISTS soutenances (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    heure TIME NOT NULL,
    salle VARCHAR(100) NOT NULL,
    jury TEXT[],
    theme_id BIGINT UNIQUE NOT NULL REFERENCES themes(id),
    etudiant_id BIGINT NOT NULL REFERENCES users(id),
    note DECIMAL(4,2),
    appreciation TEXT
);

-- Table Notifications
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    titre VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' 
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    lu BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table Memoires (Archive)
CREATE TABLE IF NOT EXISTS memoires (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(500) NOT NULL,
    auteur VARCHAR(200) NOT NULL,
    filiere VARCHAR(100) NOT NULL,
    annee INTEGER NOT NULL,
    fichier_url VARCHAR(500) NOT NULL,
    mots_cles TEXT[],
    resume TEXT,
    rapport_id BIGINT UNIQUE REFERENCES rapports(id)
);

-- Index pour améliorer les performances
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_themes_etudiant ON themes(etudiant_id);
CREATE INDEX idx_themes_filiere ON themes(filiere);
CREATE INDEX idx_themes_statut ON themes(statut);
CREATE INDEX idx_rapports_theme ON rapports(theme_id);
CREATE INDEX idx_rapports_etudiant ON rapports(etudiant_id);
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_lu ON notifications(lu);
CREATE INDEX idx_memoires_filiere ON memoires(filiere);
CREATE INDEX idx_memoires_annee ON memoires(annee);

-- Données de test (optionnel)
-- Mot de passe: "password123" hashé avec BCrypt
INSERT INTO users (email, password, nom, prenom, role, filiere) VALUES
('etudiant@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Dupont', 'Jean', 'ETUDIANT', 'MIAGE'),
('coordinateur@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Martin', 'Sophie', 'COORDINATEUR', 'MIAGE'),
('da@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Bernard', 'Pierre', 'DA', NULL);

COMMIT;
