# On part de l'image officielle Jenkins avec Java 17
FROM jenkins/jenkins:lts-jdk17

# On passe en root pour avoir le droit d'installer des paquets
USER root

# 1. Mise à jour et installation de Chromium (le navigateur)
RUN apt-get update && \
    apt-get install -y chromium && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 2. On définit la variable d'environnement pour que Karma trouve Chromium
ENV CHROME_BIN=/usr/bin/chromium

# On repasse en utilisateur Jenkins pour la sécurité
USER jenkins