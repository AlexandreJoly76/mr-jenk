# Étape 1 : Serveur Web
FROM nginx:alpine
# On copie le résultat de ta compilation Angular (dossier dist) vers Nginx
COPY dist/buy01-web/browser /usr/share/nginx/html
# On copie notre config Nginx personnalisée
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
