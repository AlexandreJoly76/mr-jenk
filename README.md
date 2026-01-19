# Buy-01 - Plateforme E-commerce

Ce projet est une application web de type e-commerce construite sur une architecture microservices. Elle comprend un frontend en Angular et un backend décomposé en plusieurs services indépendants communiquant entre eux.

## Architecture

L'architecture de Buy-01 est basée sur des microservices orchestrés par Docker Compose.

- **Gateway API (`gateway-service`)** : Point d'entrée unique pour toutes les requêtes du client. Il redirige les requêtes vers les microservices appropriés et gère les problématiques transversales comme le CORS.
- **Découverte de Services (`discovery-service`)** : Basé sur Netflix Eureka, ce service permet aux microservices de s'enregistrer et de se découvrir dynamiquement.
- **Communication Asynchrone** : Apache Kafka est utilisé pour la communication asynchrone entre les services, notamment pour la gestion des produits et des médias.
- **Base de données** : Chaque service possède sa propre base de données MongoDB, garantissant ainsi le découplage des données.

Voici le flux de communication :

`Frontend (Angular) -> Gateway API -> Microservices`

## Technologies Utilisées
### Backend
- **Java 17**
- **Spring Boot 3**
- **Spring Cloud**
- **Netflix Eureka** : Découverte de services
- **Spring Cloud Gateway** : Passerelle API
- **MongoDB** : Base de données NoSQL
- **Apache Kafka** : File de messages pour la communication asynchrone
- **Lombok** : Réduction du code répétitif

### Frontend
- **Angular 20**
- **TypeScript**

### Infrastructure
- **Docker & Docker Compose**

## Description des Services

Le backend est composé des microservices suivants :

- **`discovery-service`** : Le registre de services Eureka.
- **`gateway-service`** : La passerelle API qui expose les services au monde extérieur.
- **`user-service`** : Gère les utilisateurs, l'authentification et les autorisations.
- **`product-service`** : Gère le catalogue des produits.
- **`media-service`** : Gère le stockage et la récupération des images et autres médias.

## Démarrage Rapide

Pour lancer l'ensemble de l'application, assurez-vous d'avoir Docker et Docker Compose installés sur votre machine.

1.  Clonez ce dépôt.
2.  Placez-vous à la racine du projet.
3.  Exécutez la commande suivante pour construire et démarrer tous les conteneurs :

    ```bash
    docker-compose -f infrastructure/docker-compose.yml up --build
    ```

L'application sera alors accessible aux adresses suivantes :

- **Frontend (Angular)** : `https://localhost:4200`
- **Gateway API** : `https://localhost:8080`
- **Eureka Dashboard** : `http://localhost:8761`
- **Mongo Express** : `http://localhost:8081`

## Endpoints de l'API

La passerelle API expose les routes suivantes :

- `/user-service/**`: Route vers le service de gestion des utilisateurs.
- `/product-service/**`: Route vers le service de gestion des produits.
- `/media-service/**`: Route vers le service de gestion des médias.
