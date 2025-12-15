pipeline {
    agent any

    // Outils configurés
    tools {
        maven 'maven-3'
        jdk 'jdk-17'
        nodejs 'node-22'
    }

    // Variables d'environnement pour le pipeline
    environment {
        // Pour l'audit : on simule une adresse email
        TEAM_EMAIL = 'alexandre.joly.76300@gmail.com'
    }

    stages {
        // --- 1. BUILD BACKEND ---
        stage('Build Backend') {
            steps {
                script {
                    def services = ['discovery-service', 'gateway-service', 'user-service', 'product-service', 'media-service']
                    for (service in services) {
                        dir("microservices/${service}") {
                            echo "--- Building JAR for ${service} ---"
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }

        // --- 2. BUILD FRONTEND ---
        stage('Build Frontend') {
            steps {
                dir('frontend/buy01-web') {
                    echo "--- Installing & Building Angular ---"
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        // --- 3. DÉPLOIEMENT AUTOMATIQUE (CD) ---
        stage('Deploy to Production') {
            steps {
                dir('infrastructure') {
                    script {
                        try {
                            echo "🚀 Starting Deployment..."

                            // 1. TÉLÉCHARGEMENT DE DOCKER-COMPOSE (Version Portable)
                            // On télécharge la version officielle Linux
                            sh 'curl -SL https://github.com/docker/compose/releases/download/v2.23.3/docker-compose-linux-x86_64 -o docker-compose'

                            // 2. RENDRE EXÉCUTABLE
                            sh 'chmod +x docker-compose'

                            echo "🔄 Restarting Containers..."

                            // 3. EXÉCUTION (Note le ./ devant pour utiliser le fichier téléchargé)
                            // On utilise le fichier local, pas celui du système
                            sh './docker-compose down'
                            sh './docker-compose up -d --build'

                            echo "✅ Deployment Successful!"

                        } catch (Exception e) {
                            echo "🚨 Deployment Failed! Initiating Rollback..."

                            // Rollback avec le fichier local
                            // Le try/catch s'assure qu'on tente de redémarrer même si le build plante
                            if (fileExists('docker-compose')) {
                                sh './docker-compose up -d'
                            }

                            error "Deployment failed: ${e.message}"
                        } finally {
                            // Nettoyage : on supprime le fichier binaire pour ne pas polluer
                            sh 'rm -f docker-compose'
                        }
                    }
                }
            }
        }
    }

    // --- 4. NOTIFICATIONS ---
    post {
        success {
            echo "📧 NOTIFICATION: Build SUCCESS."
            echo "Sending email to ${TEAM_EMAIL}..."
            // Note pour l'audit : La ligne ci-dessous enverrait un vrai mail si un serveur SMTP était configuré dans Jenkins
            // mail to: TEAM_EMAIL, subject: "Success: ${env.JOB_NAME}", body: "Build is live!"
        }
        failure {
            echo "🔥 NOTIFICATION: Build FAILED."
            echo "Sending alert to Slack/Email..."
            // mail to: TEAM_EMAIL, subject: "Failure: ${env.JOB_NAME}", body: "Check logs immediately."
        }
    }
}