# PlagiCheck - Système de Gestion et Détection de Plagiat

## 📋 Description

PlagiCheck est une application web complète destinée à la gestion des mémoires de fin d'études dans les établissements d'enseignement supérieur. Elle permet la soumission des thèmes, la vérification d'unicité, le dépôt des rapports, la détection automatique de plagiat, la programmation des soutenances et l'archivage des mémoires validés.

## 🏗️ Architecture

- **Backend**: Spring Boot 3.3 + PostgreSQL
- **Frontend**: Angular 21
- **Stockage**: Supabase Storage
- **Sécurité**: JWT + BCrypt
- **Temps réel**: WebSocket

## 📁 Structure du Projet

```
SGD-Plagiat/
├── backend/                 # Spring Boot Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/plagicheck/
│   │   │   │   ├── config/          # Configuration
│   │   │   │   ├── controller/      # REST Controllers
│   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   ├── entity/          # JPA Entities
│   │   │   │   ├── repository/      # JPA Repositories
│   │   │   │   ├── service/         # Business Logic
│   │   │   │   ├── security/        # JWT & Security
│   │   │   │   ├── exception/       # Exception Handling
│   │   │   │   └── util/            # Utilities
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   └── pom.xml
│
└── frontend/                # Angular Frontend
    └── (à créer)
```

## 🚀 Démarrage Rapide

### Prérequis

- Java 21+
- Node.js 22+
- PostgreSQL 15+
- Maven 3.8+

### Backend

1. Créer la base de données PostgreSQL:
```sql
CREATE DATABASE plagicheck_db;
```

2. Configurer `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/plagicheck_db
    username: votre_username
    password: votre_password
```

3. Lancer le backend:
```bash
cd backend
mvn spring-boot:run
```

Le backend sera accessible sur `http://localhost:8080`

### Frontend

```bash
cd frontend
npm install
ng serve
```

Le frontend sera accessible sur `http://localhost:4200`

## 🔐 Sécurité

- Authentification JWT avec expiration 24h
- Mots de passe hashés avec BCrypt
- CORS configuré pour Angular
- Autorisation basée sur les rôles (ETUDIANT, COORDINATEUR, DA)

## 📊 Modèle de Données

### Entités Principales

- **User**: Utilisateurs du système
- **Theme**: Thèmes de mémoire soumis
- **Rapport**: Rapports déposés (versionnés)
- **Soutenance**: Programmation des soutenances
- **Notification**: Notifications temps réel
- **Memoire**: Archive des mémoires validés

## 🔍 Détection de Plagiat

### Algorithmes Implémentés

1. **TF-IDF + Cosinus** pour les thèmes
2. **N-Grams (n=3) + Jaccard** pour les rapports
3. **Analyse Web** pour sources externes

### Seuils de Décision

- **< 20%**: Validation automatique
- **21-40%**: Examen coordinateur
- **> 40%**: Rejet automatique

## 🎯 Fonctionnalités

### Étudiant
- ✅ Soumettre un thème
- ✅ Déposer un rapport
- ✅ Consulter les résultats
- ✅ Voir sa soutenance

### Coordinateur
- ✅ Examiner les thèmes
- ✅ Proposer validation/rejet
- ✅ Programmer les soutenances
- ✅ Gérer sa filière

### Directeur Académique
- ✅ Approuver les décisions
- ✅ Superviser toutes les filières
- ✅ Consulter les statistiques

## 📡 API REST

### Authentification
- `POST /api/auth/register` - Inscription
- `POST /api/auth/login` - Connexion

### Thèmes
- `POST /api/themes` - Soumettre un thème
- `GET /api/themes` - Liste des thèmes
- `PUT /api/themes/{id}/propose` - Proposer décision
- `PUT /api/themes/{id}/approve` - Approuver

### Rapports
- `POST /api/reports/upload` - Déposer un rapport
- `GET /api/reports/{id}` - Détails d'un rapport

### Soutenances
- `POST /api/soutenances` - Programmer une soutenance
- `GET /api/soutenances` - Liste des soutenances

## 🧪 Tests

```bash
cd backend
mvn test
```

## 📝 Licence

Ce projet est développé dans le cadre d'un projet tutoré MIAGE.

## 👥 Équipe

Projet Tutoré - MIAGE

---

**Version**: 1.0.0  
**Date**: 2024
"# SGD-Plagiat" 
