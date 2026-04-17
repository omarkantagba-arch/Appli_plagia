# Architecture Technique - PlagiCheck

## Vue d'Ensemble

PlagiCheck est une application web full-stack composée de:
- **Backend**: Spring Boot 3.3 (Java 21)
- **Frontend**: Angular 18 (TypeScript)
- **Base de données**: PostgreSQL 15
- **Stockage**: Supabase Storage
- **Sécurité**: JWT + BCrypt

## Architecture Backend

### Structure des Packages

```
com.plagicheck/
├── config/              # Configuration Spring
├── controller/          # REST Controllers
├── dto/                 # Data Transfer Objects
├── entity/              # JPA Entities
├── repository/          # JPA Repositories
├── service/             # Business Logic
├── security/            # JWT & Security
├── exception/           # Exception Handling
└── util/                # Utilities
```

### Entités Principales

#### User
- Gestion des utilisateurs (Étudiant, Coordinateur, DA)
- Authentification et autorisation

#### Theme
- Soumission et validation des thèmes
- Workflow: EN_ATTENTE → PROPOSE_VALIDATION/REJET → VALIDE/REJETE

#### Rapport
- Dépôt et analyse des rapports
- Versioning automatique
- Détection de plagiat

#### Soutenance
- Programmation des soutenances
- Gestion du jury et des notes

#### Notification
- Notifications temps réel
- Priorités: LOW, MEDIUM, HIGH

#### Memoire
- Archive des mémoires validés
- Recherche par filière et année

### Services Métier

#### AuthService
- Inscription et connexion
- Génération de tokens JWT

#### PlagiarismService
- Détection locale (TF-IDF + Cosinus)
- Détection N-Grams (Jaccard)
- Analyse web (à implémenter)

#### NotificationService (à implémenter)
- Envoi de notifications
- WebSocket pour temps réel

### Sécurité

#### JWT
- Token valide 24h
- Stockage: localStorage (frontend)
- Header: `Authorization: Bearer <token>`

#### BCrypt
- Hash des mots de passe
- Rounds: 10 (par défaut)

#### Autorisation
- Basée sur les rôles
- Guards Angular + Spring Security

## Architecture Frontend

### Structure des Modules

```
src/app/
├── core/                    # Services globaux
│   ├── services/           # AuthService, etc.
│   ├── guards/             # Route guards
│   ├── interceptors/       # HTTP interceptors
│   └── models/             # Interfaces TypeScript
├── shared/                  # Composants partagés
├── features/                # Modules fonctionnels
│   ├── auth/               # Login, Register
│   ├── dashboard/          # Dashboard
│   ├── themes/             # Gestion thèmes
│   ├── reports/            # Gestion rapports
│   ├── soutenances/        # Gestion soutenances
│   └── notifications/      # Notifications
└── app.component.ts        # Composant racine
```

### Routing

- `/login` - Connexion
- `/register` - Inscription
- `/dashboard` - Tableau de bord (protégé)
- `/themes` - Gestion des thèmes (protégé)
- `/reports` - Gestion des rapports (protégé)
- `/soutenances` - Gestion des soutenances (protégé)

### Guards

#### authGuard
- Vérifie l'authentification
- Redirige vers `/login` si non authentifié

#### roleGuard
- Vérifie le rôle de l'utilisateur
- Redirige vers `/dashboard` si non autorisé

### Intercepteurs

#### jwtInterceptor
- Ajoute le token JWT à chaque requête HTTP
- Header: `Authorization: Bearer <token>`

## Flux de Données

### Authentification

```
1. User → Login Form
2. Angular → POST /api/auth/login
3. Spring Boot → Validate credentials
4. Spring Boot → Generate JWT
5. Spring Boot → Return token + user info
6. Angular → Store token in localStorage
7. Angular → Navigate to dashboard
```

### Soumission de Thème

```
1. Étudiant → Submit theme
2. Angular → POST /api/themes
3. Spring Boot → Save theme
4. PlagiarismService → Analyze similarity
5. Spring Boot → Update plagiat_score
6. NotificationService → Notify coordinateur
7. Spring Boot → Return theme
```

### Détection de Plagiat

```
1. Upload rapport
2. Extract text (PDF/DOCX)
3. Clean text (NLP)
4. Local analysis (TF-IDF + N-Grams)
5. If score > 10% → Web analysis
6. Calculate final score
7. Auto-decision based on thresholds
```

## Base de Données

### Relations

```
User (1) ←→ (N) Theme
User (1) ←→ (N) Rapport
Theme (1) ←→ (N) Rapport
Theme (1) ←→ (1) Soutenance
Rapport (1) ←→ (1) Memoire
User (1) ←→ (N) Notification
```

### Index

- `idx_users_email` - Recherche par email
- `idx_themes_filiere` - Filtrage par filière
- `idx_themes_statut` - Filtrage par statut
- `idx_rapports_theme` - Rapports d'un thème
- `idx_notifications_user` - Notifications d'un user

## API REST

### Authentification
- `POST /api/auth/register` - Inscription
- `POST /api/auth/login` - Connexion

### Thèmes
- `POST /api/themes` - Créer un thème
- `GET /api/themes` - Liste des thèmes
- `GET /api/themes/{id}` - Détails d'un thème
- `PUT /api/themes/{id}/propose` - Proposer décision
- `PUT /api/themes/{id}/approve` - Approuver

### Rapports
- `POST /api/reports/upload` - Upload rapport
- `GET /api/reports` - Liste des rapports
- `GET /api/reports/{id}` - Détails d'un rapport

### Soutenances
- `POST /api/soutenances` - Créer soutenance
- `GET /api/soutenances` - Liste des soutenances
- `PUT /api/soutenances/{id}` - Modifier soutenance

## Déploiement

### Backend
```bash
mvn clean package
java -jar target/plagicheck-backend-1.0.0.jar
```

### Frontend
```bash
ng build --configuration production
# Déployer le contenu de dist/
```

### Docker (à implémenter)
```yaml
services:
  postgres:
    image: postgres:15
  backend:
    build: ./backend
  frontend:
    build: ./frontend
```

## Performance

### Backend
- Connection pooling (HikariCP)
- Lazy loading JPA
- Cache (à implémenter)

### Frontend
- Lazy loading des modules
- OnPush change detection
- Production build optimisé

## Sécurité

### Backend
- CSRF disabled (API REST)
- CORS configuré
- SQL injection prevention (JPA)
- XSS prevention

### Frontend
- HttpOnly cookies (recommandé)
- Sanitization Angular
- Route guards
- Token expiration handling

## Monitoring (à implémenter)

- Spring Boot Actuator
- Logs structurés
- Métriques Prometheus
- Dashboard Grafana
