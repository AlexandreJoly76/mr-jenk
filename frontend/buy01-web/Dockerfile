FROM nginx:alpine

# Copie du build Angular
COPY dist/buy01-web/browser /usr/share/nginx/html

# Copie du certificat SSL
COPY ssl /etc/nginx/ssl

# Copie de la config nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose HTTP + HTTPS
EXPOSE 443

CMD ["nginx", "-g", "daemon off;"]
