# Architecture du projet Padel

## Backend — Spring Boot

### Organisation générale

Le backend suit une architecture **feature-based** : chaque domaine métier est regroupé dans son propre package sous `be.ephec.padel.<feature>`. Chaque package contient exactement trois couches :

```
be.ephec.padel/
├── site/
│   ├── Site.java               ← Entité JPA
│   ├── SiteRepository.java     ← Accès données (Spring Data JPA)
│   ├── SiteService.java        ← Logique métier
│   └── SiteController.java     ← API REST
├── terrain/
├── membre/
├── match/
├── inscription/
├── admin/
└── fermeture/
```

### Couches

| Couche | Rôle |
|---|---|
| **Entity** | Mapping objet-relationnel via JPA/Hibernate. Annotations Lombok (`@Data`, `@Builder`, etc.) pour réduire le boilerplate. |
| **Repository** | Interface `JpaRepository<T, ID>` — Spring Data génère les requêtes SQL à partir des noms de méthodes. |
| **Service** | Logique métier, orchestration, gestion des erreurs (`RuntimeException` si entité non trouvée). |
| **Controller** | Exposition REST avec `@RestController`. Retourne `ResponseEntity` pour contrôler les statuts HTTP. |

### Features et entités

| Package | Entité principale | Relations |
|---|---|---|
| `site` | `Site` | — |
| `terrain` | `Terrain` | `@ManyToOne` → `Site` |
| `membre` | `Membre` | `@ManyToOne` → `Site` (nullable) |
| `match` | `Match` | `@ManyToOne` → `Terrain`, `Membre` |
| `inscription` | `InscriptionMatch` | `@ManyToOne` → `Match`, `Membre` |
| `admin` | `Administrateur` | `@ManyToOne` → `Site` (nullable) |
| `fermeture` | `JourFermeture` | `@ManyToOne` → `Site` (nullable = fermeture globale) |

### Frameworks et librairies backend

| Librairie | Usage |
|---|---|
| Spring Boot 3 | Framework principal |
| Spring Web | API REST (`@RestController`, `@RequestMapping`) |
| Spring Data JPA | Accès base de données |
| Hibernate | Implémentation JPA / ORM |
| Lombok | Réduction du boilerplate (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`) |
| Jakarta Persistence | Annotations JPA (`@Entity`, `@ManyToOne`, `@JoinColumn`, etc.) |
| Springdoc OpenAPI | Génération automatique de la documentation Swagger |

### Documentation API

Swagger UI disponible à : [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Frontend — Angular

### Organisation générale

Le frontend suit une architecture **feature-based** avec séparation des responsabilités :

```
src/
├── app/
│   ├── core/
│   │   ├── guards/         ← Protection des routes (authentification, rôles)
│   │   ├── interceptors/   ← Ajout automatique du token JWT aux requêtes HTTP
│   │   └── services/       ← Services partagés (auth, etc.)
│   ├── features/
│   │   ├── site/
│   │   ├── terrain/
│   │   ├── membre/
│   │   ├── match/
│   │   ├── inscription/
│   │   ├── admin/
│   │   └── fermeture/
│   └── shared/
│       └── components/     ← Composants réutilisables
```

### Couches

| Couche | Rôle |
|---|---|
| **Components** | Affichage et interaction utilisateur. Chaque feature possède ses propres composants (liste, détail, formulaire). |
| **Services** | Communication avec l'API REST via `HttpClient`. Un service par feature. |
| **Guards** | Contrôle d'accès aux routes selon l'état d'authentification ou le rôle de l'utilisateur. |
| **Interceptors** | Injection automatique du token JWT dans les en-têtes HTTP de chaque requête sortante. |

### Frameworks et librairies frontend

| Librairie | Usage |
|---|---|
| Angular | Framework principal (composants, routing, DI) |
| Angular HttpClient | Appels HTTP vers le backend |
| Angular Router | Navigation et protection des routes |
| RxJS | Programmation réactive (Observables) |
| Angular Forms | Formulaires réactifs (`ReactiveFormsModule`) |