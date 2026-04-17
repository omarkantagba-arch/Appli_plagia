-- Ajouter un coordinateur de test
-- Mot de passe: password123 (hashé avec BCrypt)

INSERT INTO users (nom, prenom, email, password, role, filiere, created_at, updated_at)
VALUES 
('Coordinateur', 'Test', 'coordinateur@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'COORDINATEUR', 'MIAGE', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Ajouter un DA de test
INSERT INTO users (nom, prenom, email, password, role, filiere, created_at, updated_at)
VALUES 
('Directeur', 'Academique', 'da@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'DA', 'MIAGE', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;
