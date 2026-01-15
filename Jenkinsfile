pipeline {
    agent any


    tools {
        maven 'maven-3'
        jdk 'jdk-17'
        nodejs 'node-22'
        'hudson.plugins.sonar.SonarRunnerInstallation' 'sonar-scanner'
    }

    environment {
        CHROME_BIN = '/usr/bin/chromium'
        SONAR_SERVER_NAME = 'sonar-server'
        FINAL_EMAIL = "${env.DEVOPS_EMAIL ?: 'ton.email@gmail.com'}"
    }

    stages {
        // --- 1. BUILD BACKEND D'ABORD (Obligatoire pour Sonar Java) ---
        stage('Build Backend (Pre-Analysis)') {
            steps {
                script {
                    def services = ['discovery-service', 'gateway-service', 'user-service', 'product-service', 'media-service','order-service']
                    for (service in services) {
                        dir("microservices/${service}") {
                            // On compile juste, sans lancer les tests (gain de temps pour l'analyse)
                            sh 'mvn clean package'
                        }
                    }
                }
            }
        }

        // --- 2. ANALYSE SONARQUBE ---
        stage('Code Quality Analysis') {
            steps {
                script {
                    echo "--- 🔍 Starting SonarQube Analysis ---"
                    def scannerHome = tool 'sonar-scanner'

                    withSonarQubeEnv(SONAR_SERVER_NAME) {
                        // LA CORRECTION EST ICI :
                        // 1. On scanne tout le dossier (.)
                        // 2. On dit à Sonar où sont les fichiers compilés (**/target/classes)
                        sh """${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=buy-01 \
                            -Dsonar.projectName=buy-01 \
                            -Dsonar.sources=. \
                            -Dsonar.host.url=http://sonarqube:9000 \
                            -Dsonar.token=${SONAR_AUTH_TOKEN} \
                            -Dsonar.java.binaries=**/target/classes"""
                    }
                }
            }
        }

        // --- 3. QUALITY GATE ---
        stage("Quality Gate") {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // --- 4. TESTS BACKEND & FRONTEND ---
        // (On refait un tour complet ou on passe au frontend,
        //  ici je laisse tes tests Frontend car le backend est déjà compilé)
        stage('Test & Build Frontend') {
            steps {
                dir('frontend/buy01-web') {
                    sh 'npm install'
                    script {
                        try {
                           sh 'npm run test -- --no-watch --no-progress --browsers=ChromeHeadless'
                        } catch (Exception e) {
                           echo "⚠️ Warning tests Frontend"
                        }
                    }
                    sh 'npm run build'
                }
            }
        }

        // --- 5. DEPLOY ---
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
             mail to: "${env.FINAL_EMAIL}", subject: "✅ SUCCESS Buy01", body: "Build OK: ${env.BUILD_URL}"
        }
        failure {
             mail to: "${env.FINAL_EMAIL}", subject: "🚨 FAILED Buy01", body: "Check logs: ${env.BUILD_URL}"
        }
    }
}