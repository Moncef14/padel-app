# Architecture du projet Padel

## Vue d'ensemble

Application de gestion de réservations de terrains de padel, multi-sites,
avec deux interfaces : membre (réservation, paiement) et administrateur
(gestion des sites, terrains, membres, statistiques).

Le projet est composé de 6 services conteneurisés via Docker :
- **sqlserver** : base SQL Server 2022
- **create-db** : création de la base `padeldb`
- **create-app-user** : création du login/user applicatif `padel_app`
  (moindre privilège)
- **backend** : API REST Spring Boot, se connecte avec le compte `padel_app`
- **sqlserver-init** : seeding automatique via `init.sql`
- **frontend** : Application Angular servie par Nginx

---

## Backend — Spring Boot

### Organisation par couche

Le backend est structuré **par couche technique**, chaque couche
regroupant tous les domaines métier :

```
be.ephec.padel/
├── PadelBackendApplication.java
├── GlobalExceptionHandler.java
├── controllers/       ← Points d'entrée REST
├── services/           ← Logique métier (+ SchedulerService, StatsService)
├── repositories/        ← Accès données (Spring Data JPA)
├── models/
│   ├── (entités JPA)
│   └── enums/           ← TypeMembre, TypeMatch, StatutMatch, StatutPaiement, RoleAdmin
├── dto/                 ← DTOs Request/Response
└── security/            ← JWT, configuration Spring Security, OpenAPI
```

### Couches

| Couche | Rôle |
|---|---|
| **Controller** | Réception des requêtes HTTP, délégation au Service, retour `ResponseEntity`. Aucune logique métier. |
| **Service** | Logique métier, règles de réservation, transactions (`@Transactional`). |
| **Repository** | `JpaRepository<T, ID>` — requêtes dérivées et JPQL personnalisées. |
| **DTO** | Sépare les objets exposés par l'API des entités JPA (évite les références circulaires, cache les données sensibles). |

### Entités principales

| Entité | Relations |
|---|---|
| `Site` | — |
| `Terrain` | `@ManyToOne` → `Site` (contrainte d'unicité numéro+site) |
| `Membre` | `@ManyToOne` → `Site` (nullable selon type GLOBAL/SITE/LIBRE) |
| `Administrateur` | `@ManyToOne` → `Site` (nullable selon rôle ADMIN_GLOBAL/ADMIN_SITE) |
| `Match` | `@ManyToOne` → `Terrain`, `Membre` (organisateur) |
| `InscriptionMatch` | `@ManyToOne` → `Match`, `Membre` |
| `JourFermeture` | `@ManyToOne` → `Site` (nullable = fermeture globale) |

### Fonctionnalités métier clés

- **Scheduler** (`SchedulerService`) : tâche planifiée quotidienne (23h59)
  qui bascule les matchs privés incomplets en public, libère les places
  non payées, applique les pénalités.
- **Statistiques** (`StatsService`) : chiffre d'affaires, taux d'occupation,
  répartition des matchs, globales ou par site.
- **Sécurité JWT** : token contenant le rôle et le site (pour les admins),
  filtre de validation à chaque requête, routes protégées par défaut.
- **Droits différenciés** : `@PreAuthorize` + filtrage par site pour
  distinguer ADMIN_GLOBAL et ADMIN_SITE.

### Frameworks et librairies backend

| Librairie | Usage |
|---|---|
| Spring Boot 3.5 | Framework principal |
| Spring Web | API REST |
| Spring Data JPA / Hibernate | Accès base de données, ORM |
| Spring Security | Authentification JWT, autorisations par rôle |
| Spring Boot Actuator | Endpoint de santé pour le healthcheck Docker |
| Lombok | Réduction du boilerplate |
| jjwt | Génération et validation des tokens JWT |
| Springdoc OpenAPI | Documentation Swagger générée automatiquement |

### Documentation API
Swagger UI : http://localhost:8080/swagger-ui.html

### Tests
41 tests unitaires (services, controllers `@WebMvcTest`, repositories
`@DataJpaTest` + H2). Lancement : `./mvnw test`

---

## Frontend — Angular

### Organisation

```
src/app/
├── models/           ← Interfaces TypeScript (miroir des DTOs backend)
├── services/          ← Appels HTTP vers l'API
├── guards/            ← Protection des routes (auth, membre, admin, admin-global)
├── interceptors/       ← Ajout automatique du token JWT
├── components/
│   ├── auth/            (login, register)
│   ├── shared/           (navbar, notification, confirm-payment-dialog)
│   ├── membre/           (mes-matchs, match-detail, reserver, matchs-publics, profil)
│   ├── admin/            (dashboard, sites, terrains, membres, matchs, fermetures, admins)
│   └── home/
└── app.routes.ts
```

### Fonctionnalités notables

- **Authentification unique adaptative** : détection automatique email
  (admin) vs matricule (membre).
- **Sélecteur de créneaux visuel** : calendrier avec dates grisées selon
  le délai minimum du type de membre, créneaux déjà réservés indisponibles.
- **Notifications centralisées** : `NotificationService` (snackbar).
- **Onglets Actifs/Historique** sur les listes de matchs.
- **Statistiques comparatives par site** sur le dashboard ADMIN_GLOBAL.

### Frameworks et librairies frontend

| Librairie | Usage |
|---|---|
| Angular 22 | Framework principal (standalone components, signals) |
| Angular Material | Composants UI |
| Angular HttpClient | Appels HTTP |
| Angular Router | Navigation et guards |

### Tests
39 tests unitaires (Vitest). Lancement : `ng test`

---

## Infrastructure — Docker

6 services orchestrés via `docker-compose.yml` :
1. **sqlserver** — base SQL Server 2022
2. **create-db** — création de la base `padeldb`
3. **create-app-user** — création du login/user applicatif `padel_app`
   (moindre privilège : `db_datareader`, `db_datawriter`, `db_ddladmin`),
   exécuté avant le démarrage du backend
4. **backend** — API Spring Boot (healthcheck via Actuator), se connecte
   avec le compte `padel_app`
5. **sqlserver-init** — seeding automatique via `init.sql`, exécuté
   après création des tables par Hibernate
6. **frontend** — Angular servi par Nginx