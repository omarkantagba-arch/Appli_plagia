-- Script pour créer des données de test

-- Insérer un thème validé pour l'étudiant (ID 1)
INSERT INTO themes (titre, description, filiere, statut, plagiat_score, etudiant_id, created_at, updated_at)
VALUES 
('Intelligence Artificielle en Santé', 
 'Étude de l''application de l''IA dans le diagnostic médical et la prédiction des maladies',
 'MIAGE',
 'VALIDE',
 5.2,
 1,
 CURRENT_TIMESTAMP,
 CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Vérifier les thèmes existants
SELECT id, titre, statut, etudiant_id FROM themes;

-- Vérifier les utilisateurs
SELECT id, email, role FROM users;
