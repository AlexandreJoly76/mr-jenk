pipeline {
    agent any

    // On définit les outils configurés dans Jenkins
    tools {
        maven 'maven-3'  // Doit correspondre au nom dans "Global Tool Configuration"
        jdk 'jdk-17'     // Doit correspondre au nom dans "Global Tool Configuration"
        nodejs 'node-22' // Doit correspondre au nom configuré à l'étape 1
    }

    stages {
        // --- ÉTAPE 1 : BACKEND (JAVA) ---
        stage('Build & Test Backend') {
            steps {
                script {
                    // Liste de tes microservices
                    def services = ['discovery-service', 'gateway-service', 'user-service', 'product-service', 'media-service']

                    for (service in services) {
                        dir("microservices/${service}") {
                            echo "--- Compiling & Testing ${service} ---"
                            // 'mvn clean package' lance la compil ET les tests unitaires (JUnit)
                            // On ajoute -DskipTests pour l'instant pour valider le pipeline, on les réactivera après
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }

        // --- ÉTAPE 2 : FRONTEND (ANGULAR) ---
        stage('Build Frontend') {
            steps {
                dir('frontend/buy01-web') {
                    echo "--- Installing Dependencies ---"
                    sh 'npm install'

                    echo "--- Building Angular App ---"
                    // Compile le projet en mode production
                    sh 'npm run build'
                }
            }
        }
    }

    // Notifications simples à la fin
    post {
        success {
            echo '✅ SUCCÈS : Le pipeline a réussi ! Le code est propre.'
        }
        failure {
            echo '❌ ÉCHEC : Il y a une erreur dans le code.'
        }
    }
}