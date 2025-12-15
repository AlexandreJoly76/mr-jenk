pipeline {
    agent any

    tools {
        maven 'maven-3'
        jdk 'jdk-17'
        nodejs 'node-22'
    }

    // Définition de variables pour les notifications (simulées)
    environment {
        TEAM_EMAIL = 'admin@buy01.com'
    }

    stages {
        // --- BACKEND ---
        stage('Build & Test Backend') {
            steps {
                script {
                    def services = ['discovery-service', 'gateway-service', 'user-service', 'product-service', 'media-service']

                    for (service in services) {
                        dir("microservices/${service}") {
                            echo "--- Testing & Building ${service} ---"
                            // PLUS DE -DskipTests !
                            // Jenkins va lancer 'ProductEntityTest.java' et réussir.
                            sh 'mvn clean package'
                        }
                    }
                }
            }
        }

        // --- FRONTEND ---
        stage('Build & Test Frontend') {
            steps {
                dir('frontend/buy01-web') {
                    echo "--- Installing ---"
                    sh 'npm install'

                    echo "--- Unit Testing ---"
                    // On lance le test Sanity
                    sh 'npm run test -- --no-watch --no-progress --browsers=ChromeHeadless'

                    echo "--- Building ---"
                    sh 'npm run build'
                }
            }
        }

        // --- DEPLOY ---
        stage('Deploy to Production') {
            steps {
                dir('infrastructure') {
                    script {
                        try {
                            // Téléchargement de docker-compose portable
                            sh 'curl -SL https://github.com/docker/compose/releases/download/v2.23.3/docker-compose-linux-x86_64 -o docker-compose'
                            sh 'chmod +x docker-compose'

                            echo "🚀 Deploying..."
                            sh './docker-compose down'
                            sh './docker-compose up -d --build'

                        } catch (Exception e) {
                            echo "🚨 Rollback..."
                            if (fileExists('docker-compose')) {
                                sh './docker-compose up -d'
                            }
                            error "Deployment failed."
                        } finally {
                            sh 'rm -f docker-compose'
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ PIPELINE SUCCESS. Notification sent to ${TEAM_EMAIL}"
        }
        failure {
            echo "❌ PIPELINE FAILED. Alert sent to ${TEAM_EMAIL}"
        }
    }
}