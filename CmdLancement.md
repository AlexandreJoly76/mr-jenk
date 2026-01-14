### Alloué la mémoire avant lancement du projet pour sonarQube
sudo sysctl -w vm.max_map_count=262144
### Lancer les services avec docker-compose
docker-compose -f infrastructure/docker-compose.yml up --build
### Lancer Jenkins (optionnel)
cd ~/mr-jenk
docker compose -f jenkins-compose.yml up -d
### Accéder au tableau de bord Eureka
echo "Eureka Dashboard: http://localhost:8761"
### Accéder à l'application Angular
echo "Angular Frontend: https://localhost:4200"
### Accéder à Mongo Express
echo "Mongo Express: http://localhost:8081"
### Accéder à la Gateway API
echo "Gateway API: https://localhost:8080"
### Fin du script de lancement
