
---

## 🔵 Backend API (Spring Boot – MongoDB – Security)

```markdown
# 🛠️ Veg'N Bio Backend API

Backend Java Spring Boot pour la plateforme Veg'N Bio.
Gère les utilisateurs, les restaurants, les repas, les événements, les réservations et la sécurité.

---

## 🧱 Stack technique

- **Java 17**
- **Spring Boot 3**
- **Spring Data MongoDB**
- **Spring Security (BasicAuth)**
- **Spring Web / Validation**
- **Lombok**
- **MapStruct**
- **Swagger / OpenAPI**
- **Docker / Docker Compose**

---

## 📦 Modules

| Module | Description |
|--------|-------------|
| **Auth Service** | Authentification BasicAuth + gestion des rôles |
| **User Service** | CRUD utilisateurs (admin, client, restaurateur, fournisseur) |
| **Restaurant Service** | Gestion des restaurants |
| **Meal Service** | Gestion des repas (CRUD + statistiques) |
| **Reservation Service** | Réservation de tables / salles avec validation anti-chevauchement |
| **Event Service** | Gestion des événements (locaux + globaux) |
| **Statistics Service** | Tableaux de bord et indicateurs pour admin et restaurateur |

---

## 🗂️ Structure du projet

src/
├── main/java/com/vegnbio/
│ ├── controller/
│ ├── model/
│ ├── repository/
│ ├── service/
│ ├── security/
│ ├── dto/
│ └── config/
├── main/resources/
│ ├── application.yml
│ └── data/
└── test/java/com/vegnbio/


---

## 🧩 Endpoints principaux

| Ressource | Endpoint | Méthode | Rôle |
|------------|-----------|----------|------|
| Auth | `/api/auth/login` | POST | Public |
| User | `/api/users` | CRUD | ADMIN |
| Meal | `/api/meals` | CRUD | RESTAURATEUR / ADMIN |
| Restaurant | `/api/restaurants` | CRUD | ADMIN / RESTAURATEUR |
| Event | `/api/events` | CRUD | RESTAURATEUR / ADMIN |
| Reservation | `/api/reservations` | CRUD | CLIENT / RESTAURATEUR |
| Stats | `/api/stats` | GET | ADMIN / RESTAURATEUR |

---

## 🧰 Commandes utiles

### Lancer en dev
```bash
mvn spring-boot:run
