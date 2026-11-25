# Gestion des produits — Spring Boot, Spring Data JPA et H2

## 1. Présentation

Ce projet est une application Spring Boot qui illustre la persistance et la gestion de produits à l'aide de **Spring Data JPA**, d'une base de données **H2 en mémoire** et d'une API REST.

L'entité `Product` représente un produit avec les propriétés suivantes :

| Attribut | Type | Description |
|---|---|---|
| `id` | `Long` | Identifiant généré automatiquement |
| `name` | `String` | Nom du produit |
| `price` | `double` | Prix du produit |
| `quantity` | `int` | Quantité en stock |

Le projet permet notamment d'ajouter des produits, de consulter la liste et un produit, et de rechercher des produits par nom. Le dépôt contient également des méthodes de recherche par critères.

> **État du projet :** les routes de création, lecture et recherche sont présentes. Les routes de mise à jour et de suppression ne sont pas encore implémentées dans `ProductRestController`. La recherche par nom et prix existe dans le repository, mais n'est pas exposée par une route REST dédiée.

## 2. Technologies et dépendances

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC — API REST
- Spring Data JPA — accès aux données et opérations CRUD
- Hibernate/JPA — mapping objet-relationnel
- H2 — base de données embarquée en mémoire
- Lombok — génération de méthodes répétitives
- Maven — gestion des dépendances et compilation
- Springdoc OpenAPI — interface Swagger UI (dépendance présente dans le `pom.xml`)

## 3. Prérequis

- JDK 21 installé
- IntelliJ IDEA (Ultimate recommandé pour le projet Spring)
- Maven (le wrapper Maven fourni peut être utilisé)
- Un client HTTP, par exemple Postman, Insomnia ou `curl`

## 4. Importer et démarrer le projet dans IntelliJ IDEA

1. Décompresser l'archive du projet.
2. Dans IntelliJ IDEA, choisir **File → Open** et sélectionner le dossier contenant `pom.xml`.
3. Laisser IntelliJ importer le projet Maven et télécharger les dépendances.
4. Vérifier que le SDK du projet est Java 21 : **File → Project Structure → Project SDK**.
5. Lancer la classe `OrmTestApplication` (située dans `net.yassine.ormtest`) avec le bouton Run.

L'application démarre sur le port **8084**.

### Avec Maven depuis un terminal

Depuis le dossier du projet :

```bash
./mvnw spring-boot:run
```

Sous Windows :

```bat
mvnw.cmd spring-boot:run
```

Pour exécuter les tests :

```bash
./mvnw test
```

## 5. Configuration de la base de données

La configuration est définie dans `src/main/resources/application.properties` :

```properties
spring.application.name=orm-test
server.port=8084
spring.datasource.url=jdbc:h2:mem:products-db
spring.h2.console.enabled=true
```

- `spring.application.name` : nom de l'application.
- `server.port` : port HTTP utilisé (8084).
- `spring.datasource.url` : crée une base H2 en mémoire nommée `products-db`.
- `spring.h2.console.enabled=true` : active la console web H2.

La base en mémoire est recréée lorsque l'application redémarre : les données ajoutées pendant une session ne sont donc pas conservées après l'arrêt.

### Console H2

Lorsque l'application est lancée, ouvrir :

http://localhost:8084/h2-console

Utiliser les paramètres suivants :

- **JDBC URL :** `jdbc:h2:mem:products-db`
- **User Name :** `sa`
- **Password :** laisser vide (configuration par défaut)

## 6. Organisation du code

```text
orm-test/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── src/
    ├── main/
    │   ├── java/net/yassine/ormtest/
    │   │   ├── OrmTestApplication.java
    │   │   ├── entities/
    │   │   │   └── Product.java
    │   │   ├── repository/
    │   │   │   └── ProductRepository.java
    │   │   └── web/
    │   │       └── ProductRestController.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/net/yassine/ormtest/
            └── OrmTestApplicationTests.java
```

### Rôle des composants

- **`Product`** : entité JPA annotée `@Entity`. `@Id` et `@GeneratedValue` définissent l'identifiant auto-généré. Lombok (`@Getter`, `@Setter`, `@Builder`, etc.) génère les méthodes usuelles.
- **`ProductRepository`** : interface qui hérite de `JpaRepository<Product, Long>`. Elle fournit automatiquement `save`, `findAll`, `findById`, `deleteById`, etc., et contient des méthodes de recherche déclarées.
- **`ProductRestController`** : expose les endpoints HTTP et délègue les opérations au repository.
- **`OrmTestApplication`** : point d'entrée Spring Boot. Un `CommandLineRunner` ajoute trois produits au démarrage et affiche le résultat d'une recherche dans la console.
- **`application.properties`** : configure l'application et la base H2.

## 7. API REST actuellement implémentée

Base URL : `http://localhost:8084`

| Méthode | Endpoint | Description |
|---|---|---|
| `GET` | `/products` | Récupérer tous les produits |
| `GET` | `/products/{id}` | Récupérer un produit par son identifiant |
| `GET` | `/find/products?kw=mot` | Rechercher les produits dont le nom contient le mot-clé |
| `POST` | `/products` | Ajouter un produit (ou mettre à jour si un ID fourni correspond, comportement de `save`) |

### 7.1 Ajouter un produit

Requête :

```http
POST http://localhost:8084/products
Content-Type: application/json
```

Corps JSON :

```json
{
  "name": "Clavier",
  "price": 250,
  "quantity": 15
}
```

L'identifiant n'est pas nécessaire : il est généré par la base. La réponse contient le produit enregistré avec son ID.

Exemple `curl` :

```bash
curl -X POST http://localhost:8084/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Clavier","price":250,"quantity":15}'
```

### 7.2 Consulter tous les produits

```http
GET http://localhost:8084/products
```

Exemple :

```bash
curl http://localhost:8084/products
```

Réponse possible (les ID dépendent de la base) :

```json
[
  {"id":1,"name":"Computer","price":6500.0,"quantity":4},
  {"id":2,"name":"Printer","price":640.0,"quantity":4},
  {"id":3,"name":"Smart Phone","price":1200.0,"quantity":34}
]
```

Les trois exemples ci-dessus sont insérés automatiquement par le `CommandLineRunner` à chaque démarrage de l'application.

### 7.3 Consulter un produit par ID

```http
GET http://localhost:8084/products/1
```

Exemple :

```bash
curl http://localhost:8084/products/1
```

**Limite actuelle :** le contrôleur utilise `findById(id).get()`. Si l'ID n'existe pas, une exception est déclenchée. Une gestion propre devrait retourner une réponse HTTP `404 Not Found`.

### 7.4 Rechercher des produits par nom

```http
GET http://localhost:8084/find/products?kw=phone
```

Exemple :

```bash
curl "http://localhost:8084/find/products?kw=phone"
```

La recherche utilise `findByNameContains`, donc le terme recherché doit apparaître dans le nom. Cette méthode n'est pas explicitement insensible à la casse. Pour une recherche insensible à la casse, le repository propose également `findByNameContainsIgnoreCase`.

## 8. Méthodes disponibles dans `ProductRepository`

En plus des méthodes CRUD héritées de `JpaRepository`, le repository définit :

```java
List<Product> findByNameContainsIgnoreCase(String kw);
List<Product> findByNameContains(String kw);
List<Product> findByNameContainsAndPriceGreaterThan(String mc, double price);

@Query("select p from Product p where p.name like :x and p.price>:y")
List<Product> search(@Param("x") String mc, @Param("y") double price);
```

- `findByNameContainsIgnoreCase` : recherche par nom, sans tenir compte des majuscules/minuscules.
- `findByNameContains` : recherche par nom, en respectant la casse selon la base.
- `findByNameContainsAndPriceGreaterThan` : nom contenant le terme donné et prix supérieur au seuil.
- `search` : requête JPQL personnalisée, nom correspondant au motif `x` et prix supérieur à `y`. Pour rechercher une partie du nom, passer un motif avec `%`, par exemple `%phone%`.

Exemple d'utilisation dans le code (pas exposée actuellement comme endpoint HTTP) :

```java
productRepository.findByNameContainsAndPriceGreaterThan("phone", 1000);
productRepository.search("%phone%", 1000);
```

## 9. Opérations CRUD demandées et état d'avancement

| Opération du sujet | État | Implémentation |
|---|---|---|
| Ajouter des produits | Implémentée | `POST /products` |
| Consulter tous les produits | Implémentée | `GET /products` |
| Consulter un produit | Implémentée | `GET /products/{id}` |
| Chercher des produits | Implémentée | `GET /find/products?kw=...` et méthodes repository |
| Mettre à jour un produit | Partiellement possible via `save`, mais route dédiée absente | À ajouter (`PUT /products/{id}`) |
| Supprimer un produit | Repository le permet, mais route absente | À ajouter (`DELETE /products/{id}`) |

## 10. Exemples de tests manuels

Lancer l'application, puis envoyer les requêtes suivantes depuis Postman ou un autre client HTTP :

1. `POST /products` avec un JSON de produit pour vérifier l'ajout.
2. `GET /products` pour voir l'ensemble des produits.
3. `GET /products/1` pour consulter un produit (adapter l'ID).
4. `GET /find/products?kw=phone` pour effectuer une recherche.
5. Tester les méthodes de recherche avancée dans le repository, ou les exposer via une route REST.
6. La mise à jour et la suppression nécessitent l'ajout des endpoints correspondants au contrôleur (voir section suivante).

## 11. Améliorations recommandées pour terminer le sujet

Pour rendre le projet entièrement conforme au sujet, ajouter les routes de mise à jour et de suppression, ainsi que la gestion du cas « produit introuvable ». Exemple de routes attendues :

| Méthode | Endpoint | Utilité |
|---|---|---|
| `PUT` | `/products/{id}` | Modifier un produit existant |
| `DELETE` | `/products/{id}` | Supprimer un produit |

Ces routes ne figurent pas encore dans le contrôleur du ZIP. Elles peuvent être ajoutées en utilisant `findById`, `save` et `deleteById` du repository.

Autres améliorations possibles :
- Ajouter `spring.jpa.hibernate.ddl-auto=update` ou une stratégie explicite de création de schéma (à choisir selon le besoin du projet).
- Valider les données entrantes (nom non vide, prix et quantité positifs) avec Bean Validation.
- Remplacer `double` par `BigDecimal` pour représenter des montants monétaires dans une application réelle.
- Ajouter des tests d'intégration pour les endpoints REST.
- Rendre la base H2 persistante sur disque si les données doivent être conservées.

## 12. Conclusion

Ce projet constitue un exemple d'application Spring Boot orientée REST, avec une entité JPA, un repository Spring Data, une base H2 et des recherches dérivées du nom des méthodes. Il permet de comprendre le mapping objet-relationnel et les opérations de base sur les produits.

Pour répondre complètement au sujet, les opérations de mise à jour et de suppression doivent encore être exposées dans `ProductRestController`.
