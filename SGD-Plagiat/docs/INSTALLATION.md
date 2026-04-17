# Guide d'Installation - PlagiCheck

## Prérequis

- **Java**: JDK 21 ou supérieur
- **Node.js**: Version 22 ou supérieure
- **PostgreSQL**: Version 15 ou supérieure
- **Maven**: Version 3.8 ou supérieure
- **Angular CLI**: `npm install -g @angular/cli`

## Installation de la Base de Données

### 1. Installer PostgreSQL

Téléchargez et installez PostgreSQL depuis [postgresql.org](https://www.postgresql.org/download/)

### 2. Créer la base de données

```bash
# Se connecter à PostgreSQL
psql -U postgres

# Exécuter le script d'initialisation
\i database/init.sql
```

Ou manuellement:

```sql
CREATE DATABASE plagicheck_db;
```

### 3. Configurer les accès

Modifiez `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/plagicheck_db
    username: votre_username
    password: votre_password
```

## Installation du Backend

### 1. Naviguer vers le dossier backend

```bash
cd backend
```

### 2. Installer les dépendances Maven

```bash
mvn clean install
```

### 3. Lancer l'application

```bash
mvn spring-boot:run
```

Le backend sera accessible sur: `http://localhost:8080`

### 4. Vérifier le fonctionnement

```bash
curl http://localhost:8080/api/auth/login
```

## Installation du Frontend

### 1. Naviguer vers le dossier frontend

```bash
cd frontend
```

### 2. Installer les dépendances npm

```bash
npm install
```

### 3. Lancer l'application

```bash
npm start
# ou
ng serve
```

Le frontend sera accessible sur: `http://localhost:4200`

## Configuration Supabase (Optionnel)

Pour le stockage des fichiers, configurez Supabase:

1. Créez un compte sur [supabase.com](https://supabase.com)
2. Créez un nouveau projet
3. Créez un bucket nommé `plagicheck-files`
4. Modifiez `application.yml`:

```yaml
supabase:
  url: https://votre-projet.supabase.co
  key: votre-cle-api
  bucket: plagicheck-files
```

## Comptes de Test

Après l'exécution du script `init.sql`, vous aurez accès à:

| Email | Mot de passe | Rôle |
|-------|--------------|------|
| etudiant@test.com | password123 | ETUDIANT |
| coordinateur@test.com | password123 | COORDINATEUR |
| da@test.com | password123 | DA |

## Vérification de l'Installation

### Backend

```bash
# Tester l'API
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"etudiant@test.com","password":"password123"}'
```

### Frontend

Ouvrez votre navigateur sur `http://localhost:4200` et connectez-vous avec un compte de test.

## Dépannage

### Erreur de connexion à la base de données

- Vérifiez que PostgreSQL est démarré
- Vérifiez les credentials dans `application.yml`
- Vérifiez que la base de données existe

### Port déjà utilisé

Backend (8080):
```yaml
server:
  port: 8081  # Changez le port
```

Frontend (4200):
```bash
ng serve --port 4201
```

### Erreur CORS

Vérifiez la configuration CORS dans `SecurityConfig.java`:
```java
configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
```

## Commandes Utiles

### Backend
```bash
# Compiler
mvn clean package

# Tests
mvn test

# Générer le JAR
mvn clean package -DskipTests
```

### Frontend
```bash
# Build production
ng build --configuration production

# Tests
ng test

# Linter
ng lint
```

## Prochaines Étapes

1. Configurer Supabase pour le stockage
2. Implémenter les modules métier (Themes, Reports, Soutenances)
3. Configurer WebSocket pour les notifications temps réel
4. Ajouter les tests unitaires et d'intégration
