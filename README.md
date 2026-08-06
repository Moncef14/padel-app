# Application de gestion de terrains de padel

Application web full-stack permettant de gérer des sites, terrains,
membres, matchs et réservations pour un club de padel. Deux interfaces :
membre et administrateur, sécurisées par authentification JWT.

Dépôt GitHub : https://github.com/Moncef14/padel-app

---

## Stack technique

| Couche | Technologie |
|---|---|
| Frontend | Angular 22 |
| Backend | Java 21 / Spring Boot 3.5 |
| Base de données | SQL Server 2022 |
| Conteneurisation | Docker / Docker Compose |
| Sécurité | Spring Security + JWT (jjwt 0.12) |

---

## Démarrage rapide

> Prérequis : Docker Desktop installé et en cours d'exécution. Aucune
> autre installation n'est nécessaire.

```bash
docker-compose up --build
```

Pour arrêter :
```bash
docker-compose down
```

Voir EXPLOITATION.md pour les comptes de test et plus de détails.

---

## URLs d'accès

| Service | URL |
|---|---|
| Frontend (Angular) | http://localhost:4200 |
| Backend API (Spring Boot) | http://localhost:8080 |
| Documentation Swagger | http://localhost:8080/swagger-ui.html |

---

## Documentation

- [ARCHITECTURE.md](ARCHITECTURE.md) — organisation du code backend et
  frontend, choix techniques
- [EXPLOITATION.md](EXPLOITATION.md) — guide de démarrage, comptes de
  test, tests automatisés

---

## Auteur

Moncef Ouassal — EPHEC 2025/2026