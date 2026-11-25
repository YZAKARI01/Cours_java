# BDCC ENSET — Application de gestion des produits avec Spring MVC

## 1. Présentation du projet

Ce projet est une application Web Java/JEE réalisée avec Spring Boot. Elle permet de consulter une liste de produits, d'ajouter un produit et de supprimer un produit. L'accès aux pages est protégé par Spring Security avec deux rôles : `USER` et `ADMIN`.

Le projet fourni utilise notamment :

- Java 21
- Spring Boot 3.4.5
- Spring MVC / Spring Web
- Spring Data JPA
- Hibernate (implémentation JPA utilisée par Spring Boot)
- H2 (base de données en mémoire)
- MySQL Connector/J (dépendance disponible, mais la configuration actuelle utilise H2)
- Thymeleaf
- Thymeleaf Layout Dialect
- Bootstrap 5.3.5 via WebJars
- Lombok
- Spring Security 6
- Spring Validation
- Maven

> Périmètre de ce guide : démarrage, compréhension de l'architecture, consultation, ajout, suppression, validation et sécurité. La recherche et la modification de produits ne sont pas couvertes ici ; elles pourront être ajoutées dans une évolution ultérieure.

---

## 2. Prérequis

Avant de démarrer :

1. Installer un JDK 21.
2. Installer IntelliJ IDEA.
3. Ouvrir le dossier du projet qui contient `pom.xml`.
4. Vérifier que le projet est importé comme projet Maven.
5. Attendre la synchronisation et le téléchargement des dépendances.

Dans IntelliJ, vérifier le JDK dans :

`File → Project Structure → Project → SDK`

Sélectionner Java 21.

Le projet contient le Maven Wrapper (`mvnw` et `mvnw.cmd`). Il est donc possible d'utiliser le Maven fourni avec le projet sans installer Maven globalement.

---

## 3. Démarrer le projet dans IntelliJ

1. Ouvrir le dossier `bdcc-enset-spring-mvc`.
2. Ouvrir le panneau **Maven**.
3. Cliquer sur **Reload All Maven Projects** si les dépendances ne sont pas chargées.
4. Ouvrir la classe :

   `src/main/java/net/yassine/bdccensetspringmvc/BdccEnsetSpringMvcApplication.java`

5. Cliquer sur le triangle vert à côté de `main`, puis choisir **Run**.
6. Attendre dans la console le démarrage de Spring Boot et de Tomcat.

Le port est défini à `8094` dans `application.properties`. L'adresse principale est donc :

**http://localhost:8094**

Si le port est déjà utilisé, arrêter l'autre application qui l'utilise ou modifier `server.port` dans `application.properties`.

### Lancement avec le Maven Wrapper

Depuis le terminal IntelliJ, à la racine du projet :

- Windows :

  ```powershell
  .\mvnw.cmd spring-boot:run
  ```

- macOS / Linux :

  ```bash
  ./mvnw spring-boot:run
  ```

### Exécuter les tests

- Windows :

  ```powershell
  .\mvnw.cmd test
  ```

- macOS / Linux :

  ```bash
  ./mvnw test
  ```

Le test fourni `BdccEnsetSpringMvcApplicationTests.contextLoads()` vérifie que le contexte Spring Boot peut être chargé. Ce n'est pas encore un test détaillé des opérations DAO.

---

## 4. Configuration de la base de données

Le fichier concerné est :

`src/main/resources/application.properties`

Configuration fournie :

```properties
spring.application.name=bdcc-enset-spring-mvc
spring.datasource.url=jdbc:h2:mem:products-db
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
server.port=8094
spring.h2.console.enabled=true
```

### Explication des propriétés

| Propriété | Utilité |
|---|---|
| `spring.application.name` | Nom de l'application Spring Boot |
| `spring.datasource.url` | URL de connexion à la base H2 |
| `spring.datasource.username` | Utilisateur de la base |
| `spring.datasource.password` | Mot de passe H2, vide dans cette configuration |
| `spring.jpa.hibernate.ddl-auto=update` | Demande à Hibernate de mettre à jour le schéma selon les entités |
| `server.port=8094` | Port HTTP du serveur intégré |
| `spring.h2.console.enabled=true` | Active la console Web H2 |

### Console H2

Lorsque l'application fonctionne, ouvrir :

**http://localhost:8094/h2-console**

Paramètres de connexion :

- JDBC URL : `jdbc:h2:mem:products-db`
- User Name : `sa`
- Password : laisser vide

Cliquer sur **Connect**.

H2 est configurée ici en mémoire (`mem`). Les données ne sont donc pas conservées après l'arrêt de l'application. Les produits d'exemple sont recréés au démarrage par le `CommandLineRunner`.

### À propos de MySQL

Le connecteur MySQL est présent dans `pom.xml`, mais le fichier `application.properties` fourni configure H2. La présence du connecteur ne signifie pas que l'application utilise déjà MySQL. Pour passer à MySQL, il faudra définir une URL JDBC MySQL, un utilisateur, un mot de passe et vérifier que le serveur MySQL est démarré.

---

## 5. Structure du projet

Les fichiers principaux sont organisés ainsi :

```text
src/
├── main/
│   ├── java/net/yassine/bdccensetspringmvc/
│   │   ├── BdccEnsetSpringMvcApplication.java
│   │   ├── entities/
│   │   │   └── Product.java
│   │   ├── repository/
│   │   │   └── ProductRepository.java
│   │   ├── web/
│   │   │   └── ProductController.java
│   │   └── sec/
│   │       └── SecurityConfig.java
│   └── resources/
│       ├── application.properties
│       └── templates/
│           ├── layout1.html
│           ├── products.html
│           ├── login.html
│           ├── notAuthorized.html
│           └── new-product.html
└── test/
    └── java/net/yassine/bdccensetspringmvc/
        └── BdccEnsetSpringMvcApplicationTests.java
```

### Rôle des dossiers

- `entities` : classes Java qui représentent les données persistées.
- `repository` : interfaces d'accès aux données avec Spring Data JPA.
- `web` : contrôleurs Spring MVC qui reçoivent les requêtes HTTP et choisissent les vues.
- `sec` : configuration de l'authentification et des autorisations.
- `resources/templates` : vues HTML traitées par Thymeleaf.
- `test` : tests automatisés.

---

## 6. Explication des fichiers Java

### 6.1 `BdccEnsetSpringMvcApplication.java`

C'est la classe principale et le point d'entrée de l'application.

```java
@SpringBootApplication
public class BdccEnsetSpringMvcApplication {
    public static void main(String[] args) {
        SpringApplication.run(BdccEnsetSpringMvcApplication.class, args);
    }
}
```

- `@SpringBootApplication` active la configuration automatique de Spring Boot, le scan des composants et la configuration de l'application.
- `SpringApplication.run(...)` démarre le contexte Spring et le serveur Web intégré.

Cette classe contient aussi un bean `CommandLineRunner` :

```java
@Bean
public CommandLineRunner start(ProductRepository productRepository) {
    return args -> {
        // Enregistrement des produits d'exemple
    };
}
```

`CommandLineRunner` exécute son code après le démarrage du contexte Spring. Ici, il enregistre trois produits d'exemple :

| Nom | Prix | Quantité |
|---|---:|---:|
| Computer | 5400 | 12 |
| Printer | 1200 | 11 |
| Smart Phone | 12000 | 33 |

Il affiche ensuite les produits avec `findAll()` et `System.out.println()`.

**À retenir :** le `CommandLineRunner` sert à initialiser des données de démonstration. Dans la version fournie, les trois produits sont enregistrés à chaque démarrage. Avec H2 en mémoire, la base est recréée au redémarrage ; avec une base persistante comme MySQL, il serait préférable de vérifier si les produits existent déjà avant de les insérer afin d'éviter les doublons.

### 6.2 `entities/Product.java`

Cette classe représente l'entité JPA `Product`, qui correspond à une table relationnelle gérée par Hibernate.

Champs présents :

| Champ | Type | Signification |
|---|---|---|
| `id` | `Long` | Identifiant du produit |
| `name` | `String` | Nom du produit |
| `price` | `double` | Prix |
| `quantity` | `double` | Quantité |

Annotations utilisées :

- `@Entity` : indique à JPA que la classe est une entité persistante.
- `@Id` : désigne la clé primaire.
- `@GeneratedValue` : demande la génération de l'identifiant.
- `@NotEmpty` : interdit une valeur vide pour le nom.
- `@Size(min = 3, max = 50)` : impose une longueur du nom entre 3 et 50 caractères.
- `@Min(0)` : impose une valeur minimale de zéro au prix.
- `@Min(1)` : impose une valeur minimale de un à la quantité.
- `@Getter` et `@Setter` : Lombok génère les accesseurs.
- `@NoArgsConstructor` : Lombok génère un constructeur sans argument.
- `@AllArgsConstructor` : Lombok génère un constructeur avec tous les champs.
- `@ToString` : Lombok génère une représentation textuelle de l'objet.
- `@Builder` : Lombok permet de construire un objet avec une syntaxe de type `Product.builder().name(...).build()`.

**À retenir :** les annotations JPA concernent la persistance ; les annotations de validation contrôlent les valeurs ; Lombok réduit le code répétitif.

Remarque : dans ce projet fourni, `quantity` est de type `double`. Pour un stock compté en unités entières, `Integer` serait souvent plus adapté, mais il ne faut pas changer le modèle sans vérifier les conséquences sur les formulaires et la base.

### 6.3 `repository/ProductRepository.java`

```java
public interface ProductRepository
        extends JpaRepository<Product, Long> {
}
```

Cette interface constitue la couche d'accès aux données (DAO).

- `Product` est le type d'entité géré.
- `Long` est le type de son identifiant.
- `JpaRepository` fournit les opérations courantes sans devoir écrire leur implémentation.

Méthodes héritées importantes :

| Méthode | Rôle |
|---|---|
| `save(product)` | Insérer ou mettre à jour une entité |
| `findAll()` | Récupérer tous les produits |
| `findById(id)` | Rechercher un produit par identifiant |
| `deleteById(id)` | Supprimer par identifiant |
| `count()` | Compter les enregistrements |
| `existsById(id)` | Vérifier l'existence d'un identifiant |

Spring Data JPA crée automatiquement l'implémentation de cette interface au démarrage.

### 6.4 `web/ProductController.java`

Cette classe est un contrôleur Spring MVC.

- `@Controller` : indique que la classe traite des requêtes Web et retourne notamment des noms de vues Thymeleaf.
- `@Autowired` : injecte le `ProductRepository` géré par Spring.
- `Model` : transporte les données du contrôleur vers la vue.
- `@GetMapping` : associe une méthode à une requête HTTP GET.
- `@PostMapping` : associe une méthode à une requête HTTP POST.
- `@RequestParam` : récupère un paramètre de requête.
- `@Valid` : déclenche les validations déclarées sur l'objet.
- `BindingResult` : contient les erreurs de validation.
- `@PreAuthorize` : vérifie une règle d'autorisation avant l'exécution de la méthode.

#### Méthodes et routes du contrôleur

| Méthode | Route | Rôle |
|---|---|---|
| `home()` | `GET /` | Redirige vers `/user/index` |
| `index(model)` | `GET /user/index` | Récupère tous les produits et affiche `products.html` |
| `delete(id)` | `POST /admin/delete?id=...` | Supprime le produit puis redirige vers la liste |
| `newProduct(model)` | `GET /admin/newProduct` | Prépare un objet Product vide et affiche le formulaire |
| `saveProduct(...)` | `POST /admin/saveProduct` | Valide et enregistre le produit |
| `notAuthorized()` | `GET /notAuthorized` | Affiche la page d'accès refusé |
| `login()` | `GET /login` | Affiche la page de connexion personnalisée |
| `logout(session)` | `GET /logout` | Méthode personnalisée présente dans le code fourni |

#### Détail du fonctionnement

**Affichage :** `index()` appelle `productRepository.findAll()`, place le résultat dans le modèle sous le nom `productList`, puis retourne `"products"`. Thymeleaf affiche alors `products.html`.

**Ajout :** `newProduct()` place un nouvel objet `Product` vide dans le modèle sous le nom `product`, puis retourne `"new-product"`.

**Enregistrement :** `saveProduct()` reçoit le formulaire, déclenche la validation avec `@Valid`, examine `BindingResult`, retourne le formulaire si des erreurs existent et appelle `productRepository.save(product)` si les données sont valides.

**Suppression :** `delete()` reçoit l'identifiant avec `@RequestParam`, appelle `deleteById(id)` puis redirige vers `/user/index`.

**Redirection :** `redirect:/user/index` demande au navigateur de faire une nouvelle requête vers la liste.

### 6.5 `sec/SecurityConfig.java`

Cette classe configure Spring Security.

Annotations :

- `@Configuration` : indique une classe de configuration Spring.
- `@EnableWebSecurity` : active la configuration de sécurité Web.
- `@EnableGlobalMethodSecurity(prePostEnabled = true)` : active les annotations de sécurité sur les méthodes, notamment `@PreAuthorize`. Cette annotation est ancienne ; avec Spring Security 6, son équivalent moderne est `@EnableMethodSecurity`.

#### Encodage des mots de passe

Le bean `PasswordEncoder` retourne un `BCryptPasswordEncoder`.

BCrypt est un algorithme de hachage de mots de passe. Les mots de passe ne sont pas comparés comme du texte brut lors de l'authentification : Spring Security vérifie le mot de passe fourni à l'aide de l'encodeur.

#### Utilisateurs en mémoire

Le projet déclare trois utilisateurs dans `InMemoryUserDetailsManager` :

| Identifiant | Mot de passe de démonstration | Rôles |
|---|---|---|
| `user1` | `1234` | `USER` |
| `user2` | `1234` | `USER` |
| `admin` | `1234` | `USER`, `ADMIN` |

Ces comptes sont définis en mémoire dans le code, et non stockés dans la base H2.

#### Configuration HTTP

- `formLogin(...)` définit `/login` comme page de connexion personnalisée et autorise l'accès à cette page.
- `csrf(Customizer.withDefaults())` conserve la protection CSRF activée.
- `requestMatchers("/public/**", "/webjars/**").permitAll()` autorise les ressources publiques et les fichiers Bootstrap servis par WebJars.
- `anyRequest().authenticated()` exige une authentification pour les autres requêtes.
- `accessDeniedPage("/notAuthorized")` indique la page à afficher en cas d'accès refusé.

Les rôles des méthodes du contrôleur sont contrôlés par `@PreAuthorize`.

**Différence à connaître :**
- Authentification : vérifier qui est l'utilisateur.
- Autorisation : vérifier ce que cet utilisateur a le droit de faire.

---

## 7. Explication des vues Thymeleaf

Les vues HTML se trouvent dans :

`src/main/resources/templates/`

Thymeleaf permet de produire des pages HTML dynamiques à partir des données du modèle Spring MVC.

### 7.1 `layout1.html`

C'est le modèle commun de mise en page.

Il contient :

- La déclaration des espaces de noms Thymeleaf.
- Les liens CSS et JavaScript de Bootstrap servis par WebJars.
- La barre de navigation.
- L'affichage du nom de l'utilisateur connecté avec `sec:authentication="name"`.
- Le fragment `layout:fragment="content1"` dans lequel les pages filles insèrent leur contenu.

Le dialecte Thymeleaf Layout est activé par `thymeleaf-layout-dialect` dans `pom.xml`.

### 7.2 `products.html`

Cette vue affiche la liste des produits.

- `layout:decorate="layout1"` indique qu'elle utilise le layout.
- `layout:fragment="content1"` définit le contenu qui sera inséré dans le layout.
- `th:each="p : ${productList}"` parcourt la liste transmise par le contrôleur.
- `th:text="${p.name}"`, par exemple, affiche le nom du produit.
- `sec:authorize="hasRole('ADMIN')"` masque le bouton d'ajout et les actions de suppression aux utilisateurs qui ne possèdent pas le rôle ADMIN.
- Le formulaire de suppression envoie une requête POST vers `/admin/delete` avec l'identifiant du produit.

Masquer un bouton dans le HTML améliore l'interface, mais ne remplace pas la vérification côté serveur. La vraie protection est assurée par `@PreAuthorize` et Spring Security.

### 7.3 `new-product.html`

Cette vue contient le formulaire de création d'un produit.

Le contrôleur fournit l'objet `product`. Le formulaire envoie les valeurs vers `/admin/saveProduct`.

Les champs correspondent aux propriétés de l'entité :

- `name`
- `price`
- `quantity`

La validation est déclenchée dans le contrôleur par `@Valid`. Les erreurs peuvent être affichées avec `th:errors` lorsque les champs sont correctement liés à l'objet du modèle.

### 7.4 `login.html`

C'est la page de connexion personnalisée configurée dans `SecurityConfig`.

Elle permet à l'utilisateur de saisir son nom d'utilisateur et son mot de passe. Spring Security traite ensuite l'authentification.

### 7.5 `notAuthorized.html`

Cette vue est utilisée lorsque l'utilisateur est authentifié mais ne dispose pas des autorisations nécessaires pour accéder à une ressource.

---

## 8. Parcours de navigation et URLs

L'application écoute sur le port `8094`.

| URL | Méthode HTTP | Fonction |
|---|---|---|
| `http://localhost:8094/` | GET | Redirection vers `/user/index` |
| `http://localhost:8094/login` | GET | Page de connexion |
| `http://localhost:8094/user/index` | GET | Liste des produits |
| `http://localhost:8094/admin/newProduct` | GET | Formulaire d'ajout |
| `http://localhost:8094/admin/saveProduct` | POST | Enregistrement du formulaire |
| `http://localhost:8094/admin/delete?id=1` | POST | Suppression du produit d'identifiant 1 |
| `http://localhost:8094/notAuthorized` | GET | Page d'accès refusé |
| `http://localhost:8094/h2-console` | GET | Console H2 |

Les routes d'ajout et de suppression doivent être utilisées via leurs formulaires HTML, et non en saisissant simplement leur URL dans la barre du navigateur.

### Scénario de démonstration conseillé

1. Démarrer l'application dans IntelliJ.
2. Ouvrir `http://localhost:8094`.
3. La redirection conduit à la page protégée `/user/index`.
4. Spring Security affiche la page `/login` si l'utilisateur n'est pas encore connecté.
5. Se connecter avec `admin` et `1234`.
6. Consulter les trois produits d'exemple.
7. Cliquer sur **New Product**.
8. Remplir le formulaire et cliquer sur **Save**.
9. Vérifier que le produit apparaît dans la liste.
10. Cliquer sur **Delete** pour supprimer un produit.
11. Tester les comptes `user1` et `user2` pour constater qu'ils peuvent consulter la liste, mais ne doivent pas pouvoir effectuer les opérations réservées à ADMIN.

---

## 9. Sécurité : désactivation temporaire puis activation

L'énoncé du TP demande de désactiver la protection par défaut de Spring Security, puis de sécuriser l'application.

Dans le projet fourni, la ligne suivante est commentée :

```java
//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@SpringBootApplication
```

Pour désactiver temporairement l'auto-configuration de Spring Security dans le cadre de l'étape pédagogique, on peut décommenter l'exclusion :

```java
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
```

Il faut alors importer :

```java
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
```

Cette désactivation est temporaire et ne doit pas être conservée pour la démonstration finale de sécurité. Elle peut aussi rendre inopérantes les configurations de sécurité attendues ; il faut donc remettre `@SpringBootApplication` sans exclusion et vérifier le fonctionnement de `SecurityConfig`.

Ne pas exclure `DataSourceAutoConfiguration` : l'application a besoin de sa source de données H2.

---

## 10. Comprendre la validation du formulaire

La validation est déclarée dans `Product.java` avec les annotations Jakarta Validation :

- `@NotEmpty` : le nom ne doit pas être vide.
- `@Size(min = 3, max = 50)` : le nom doit contenir de 3 à 50 caractères.
- `@Min(0)` : le prix doit être supérieur ou égal à zéro.
- `@Min(1)` : la quantité doit être supérieure ou égale à un.

Dans le contrôleur :

```java
public String saveProduct(
        @Valid Product product,
        BindingResult bindingResult,
        Model model) {
    if (bindingResult.hasErrors()) {
        return "new-product";
    }
    productRepository.save(product);
    return "redirect:/admin/newProduct";
}
```

- `@Valid` déclenche les contraintes.
- `BindingResult` reçoit les erreurs.
- `hasErrors()` permet de savoir si le formulaire contient des valeurs invalides.
- Si des erreurs existent, le formulaire est réaffiché.
- Sinon, le produit est enregistré.

Pour afficher les erreurs dans Thymeleaf, il faut associer les champs à l'objet avec `th:object` et `th:field`, puis afficher les messages avec `th:errors`.

---

## 11. Questions possibles du professeur

### Q1. Quel est le rôle de Spring Boot ?

Spring Boot simplifie la configuration et le démarrage d'une application Spring. Il fournit notamment l'auto-configuration et un serveur Web intégré.

### Q2. Quelle est la différence entre Spring MVC et Spring Data JPA ?

Spring MVC gère les requêtes HTTP, les contrôleurs et les vues. Spring Data JPA facilite l'accès et la persistance des données.

### Q3. Qu'est-ce que JPA ?

JPA est une spécification Java qui définit le mapping entre les objets Java et les tables relationnelles.

### Q4. Quelle est la différence entre JPA et Hibernate ?

JPA définit les règles et les interfaces de persistance. Hibernate est une implémentation de cette spécification.

### Q5. Quel est le rôle de `@Entity` ?

Elle indique que la classe Java est une entité persistante gérée par JPA.

### Q6. Quel est le rôle de `@Id` ?

Elle désigne la clé primaire de l'entité.

### Q7. Quel est le rôle de `@GeneratedValue` ?

Elle indique que la valeur de l'identifiant est générée automatiquement.

### Q8. Quel est le rôle de `JpaRepository` ?

Il fournit les opérations CRUD et d'autres méthodes d'accès aux données sans devoir écrire leur implémentation.

### Q9. Pourquoi utiliser `CommandLineRunner` ?

Pour exécuter du code après le démarrage de l'application, par exemple pour insérer des données de démonstration.

### Q10. Quel est le rôle de Thymeleaf ?

Thymeleaf génère des pages HTML dynamiques en utilisant les données transmises par le contrôleur.

### Q11. Quel est le rôle de `Model` ?

Il transporte les attributs du contrôleur vers la vue.

### Q12. Quelle est la différence entre GET et POST ?

GET sert généralement à consulter une ressource. POST sert à transmettre des données ou déclencher une opération côté serveur.

### Q13. Quel est le rôle de `@Valid` ?

Elle déclenche la validation de l'objet selon les contraintes déclarées.

### Q14. Quel est le rôle de `BindingResult` ?

Il contient les erreurs de validation et de liaison des données du formulaire.

### Q15. Quelle est la différence entre authentification et autorisation ?

L'authentification vérifie l'identité de l'utilisateur. L'autorisation vérifie ses permissions.

### Q16. Pourquoi utiliser BCrypt ?

BCrypt permet de hacher les mots de passe avant leur utilisation dans le mécanisme d'authentification.

### Q17. Quelle est la différence entre USER et ADMIN dans ce projet ?

Les deux rôles peuvent consulter les produits. ADMIN dispose en plus des autorisations d'ajout et de suppression.

### Q18. Pourquoi utiliser H2 ?

H2 est une base légère et facile à démarrer, pratique pour le développement et les tests sans installer un serveur de base de données.

### Q19. Les données H2 sont-elles conservées après l'arrêt ?

Non, pas avec l'URL `jdbc:h2:mem:products-db`, car la base est en mémoire.

### Q20. Quel est le rôle de Bootstrap ?

Bootstrap fournit des composants et des classes CSS pour construire une interface Web responsive.

---

## 12. Points importants à connaître avant la présentation

1. Le port de l'application est `8094`.
2. La base utilisée actuellement est H2 en mémoire.
3. Les produits d'exemple sont créés au démarrage par `CommandLineRunner`.
4. Les comptes de sécurité sont stockés en mémoire, pas dans la base.
5. Les mots de passe de démonstration sont `1234`.
6. `admin` possède les rôles `USER` et `ADMIN`.
7. `user1` et `user2` possèdent uniquement le rôle `USER`.
8. La liste est accessible via `GET /user/index`.
9. L'ajout utilise `GET /admin/newProduct`, puis `POST /admin/saveProduct`.
10. La suppression utilise `POST /admin/delete` avec le paramètre `id`.
11. La validation est définie dans l'entité et déclenchée par `@Valid`.
12. Le contrôle des rôles est réalisé côté serveur avec `@PreAuthorize`.
13. Bootstrap est servi par WebJars.
14. Le layout commun est `layout1.html`.
15. La recherche et la modification ne font pas partie du périmètre de ce guide.

---

## 13. Dépannage

### L'application ne démarre pas

- Vérifier que le JDK sélectionné est Java 21.
- Vérifier que le port 8094 n'est pas déjà utilisé.
- Recharger les projets Maven.
- Lire la première cause d'erreur dans la console IntelliJ.

### IntelliJ affiche `Cannot resolve symbol`

- Vérifier les dépendances dans `pom.xml`.
- Ouvrir le panneau Maven.
- Cliquer sur **Reload All Maven Projects**.
- Vérifier que le téléchargement Maven s'est terminé sans erreur.

### La page n'est pas accessible

- Vérifier que l'application est toujours en cours d'exécution.
- Vérifier le port 8094.
- Vérifier l'URL.
- Si une redirection vers `/login` apparaît, c'est le comportement attendu lorsque l'utilisateur n'est pas authentifié.

### Connexion refusée

- Vérifier l'identifiant et le mot de passe.
- Comptes fournis : `user1 / 1234`, `user2 / 1234`, `admin / 1234`.
- Vérifier que le bean `InMemoryUserDetailsManager` est bien chargé.

### Accès refusé

Un utilisateur peut être authentifié sans avoir le rôle requis. Pour les actions d'administration, utiliser le compte `admin`.

### Les produits ne sont pas conservés

C'est normal avec H2 en mémoire. Le `CommandLineRunner` réinsère les données d'exemple au prochain démarrage.

---

## 14. Résumé de l'architecture à présenter oralement

Le navigateur envoie une requête HTTP. Spring Security vérifie l'authentification et les autorisations. Le contrôleur Spring MVC reçoit ensuite la requête, appelle `ProductRepository` pour accéder aux données et transmet les résultats à Thymeleaf via `Model`. Spring Data JPA utilise Hibernate pour réaliser les opérations sur la base H2. Enfin, Thymeleaf génère la page HTML, mise en forme avec Bootstrap, qui est renvoyée au navigateur.

**Phrase courte à retenir :**

> Spring MVC gère les requêtes et les vues, Spring Data JPA facilite l'accès aux données, Hibernate réalise le mapping objet-relationnel, Thymeleaf génère les pages HTML, Bootstrap gère la présentation et Spring Security protège l'application.

---

## 15. Ressource vidéo du TP

Vidéo indiquée dans l'énoncé :

https://www.youtube.com/watch?v=FHy7raIldgg
