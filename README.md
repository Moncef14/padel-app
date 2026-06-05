# Application de gestion de terrains de padel

Application web full-stack permettant de gérer des sites, terrains, membres, matchs et réservations pour un club de padel. L'accès à l'interface d'administration est sécurisé par authentification JWT.

Dépôt GitHub : [https://github.com/Moncef14/padel-app](https://github.com/Moncef14/padel-app)

---

## Stack technique

| Couche | Technologie |
|---|---|
| Frontend | Angular |
| Backend | Java 21 / Spring Boot 3.5 |
| Base de données | SQL Server |
| Conteneurisation | Docker / Docker Compose |
| Sécurité | Spring Security + JWT (jjwt 0.12) |

---

## Démarrage rapide

> **Prérequis** : Docker Desktop installé et en cours d'exécution. Aucune autre installation n'est nécessaire.

Depuis la racine du projet :

```bash
docker-compose up --build
```

La première exécution télécharge les images et compile les projets — prévoir quelques minutes. Les démarrages suivants sont plus rapides.

Pour arrêter :

```bash
docker-compose down
```

---

## Démarrage sans Docker (développement local)

**Prérequis :** Java 21, SQL Server 2022, SSMS

**1. Créer la base de données dans SSMS :**

```sql
CREATE DATABASE padeldb COLLATE French_CI_AI;
```

**2. Créer le user applicatif dans SSMS :**

```sql
USE master;
CREATE LOGIN padel_admin_user WITH PASSWORD = 'PadelAdmin2026';
USE padeldb;
CREATE USER padel_admin_user FOR LOGIN padel_admin_user;
GRANT CONTROL ON DATABASE::padeldb TO padel_admin_user;
```

**3. Exécuter le script de données :** ouvrir et exécuter `init.sql` dans SSMS.

**4. Lancer le backend :**

```powershell
cd padel-backend
.\mvnw spring-boot:run
```

**5. API disponible sur** [http://localhost:8080](http://localhost:8080)

---

## URLs d'accès

| Service | URL |
|---|---|
| Frontend (Angular) | [http://localhost:80](http://localhost:80) |
| Backend API (Spring Boot) | [http://localhost:8080](http://localhost:8080) |
| Documentation Swagger | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |

---

## Documentation

- [ARCHITECTURE.md](ARCHITECTURE.md) — organisation du code backend et frontend, choix techniques
- [EXPLOITATION.md](EXPLOITATION.md) — guide de démarrage, arrêt et tests

---

## Auteur

Moncef Ouassal — EPHEC 2025/2026