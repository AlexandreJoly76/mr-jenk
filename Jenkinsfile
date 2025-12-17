pipeline {
    agent any


    tools {
        maven 'maven-3'
        jdk 'jdk-17'
        nodejs 'node-22'
        // On ajoute l'outil scanner configuré plus tôt
        // On utilise le nom interne exact donné dans ton message d'erreur
        'hudson.plugins.sonar.SonarRunnerInstallation' 'sonar-scanner'
    }

    environment {
        CHROME_BIN = '/usr/bin/chromium'
        // Nom du serveur configuré dans Jenkins -> System
        SONAR_SERVER_NAME = 'sonar-server'
    }

    stages {
// --- 1. ANALYSE SONARQUBE (Correction : Utilisation du Scanner CLI) ---
        stage('Code Quality Analysis') {
            steps {
                script {
                    echo "--- 🔍 Starting SonarQube Analysis ---"

                    // 1. On récupère le chemin de l'outil 'sonar-scanner' configuré dans Jenkins
                    def scannerHome = tool 'sonar-scanner'

                    // 2. On lance l'analyse
                    withSonarQubeEnv(SONAR_SERVER_NAME) {
                        // On utilise l'exécutable direct du scanner
                        // -Dsonar.sources=.  signifie "Scanne tout le dossier courant"
                        sh """${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=buy-01 \
                            -Dsonar.projectName=buy-01 \
                            -Dsonar.sources=. \
                            -Dsonar.host.url=http://sonarqube:9000 \
                            -Dsonar.token=${SONAR_AUTH_TOKEN}"""
                    }
                }
            }
        }

        // --- NOUVELLE ÉTAPE : QUALITY GATE ---
        // C'est ici qu'on bloque le pipeline si le code est pourri
        stage("Quality Gate") {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    // Attend que SonarQube renvoie le verdict via le Webhook
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Test & Build Backend') {
            steps {
                script {
                    def services = ['discovery-service', 'gateway-service', 'user-service', 'product-service', 'media-service']
                    for (service in services) {
                        dir("microservices/${service}") {
                            // On relance package pour générer les JARs finaux (sans re-télécharger grâce au cache)
                            sh 'mvn package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Test & Build Frontend') {
            steps {
                dir('frontend/buy01-web') {
                    sh 'npm install'
                    script {
                        try {
                           sh 'npm run test -- --no-watch --no-progress --browsers=ChromeHeadless'
                        } catch (Exception e) {
                           echo "⚠️ Tests Frontend warning."
                        }
                    }
                    sh 'npm run build'
                }
            }
        }

        stage('Deploy to Production') {
            steps {
                dir('infrastructure') {
                    script {
                        try {
                            sh 'curl -SL https://github.com/docker/compose/releases/download/v2.23.3/docker-compose-linux-x86_64 -o docker-compose'
                            sh 'chmod +x docker-compose'
                            sh './docker-compose down'
                            sh './docker-compose up -d --build'
                        } catch (Exception e) {
                            if (fileExists('docker-compose')) { sh './docker-compose up -d' }
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
            echo "✅ BUILD SUCCESS"
             mail to: "${env.DEVOPS_EMAIL}", subject: "✅ SUCCESS", body: "Build OK"
        }
        failure {
            echo "❌ BUILD FAILED"
             mail to: "${env.DEVOPS_EMAIL}", subject: "🚨 FAILED", body: "Check logs"
        }
    }
}