pipeline {
    agent any
    
    stages {
        stage('Build') { 
            tools {
                jdk 'Java 8'
                maven 'Maven'
            }
            steps {
                //sh 'mvn -B -DskipTests clean package dependency:copy-dependencies' 
                sh 'java -version' 
                sh 'mvn -B -DskipTests clean package dependency:copy-dependencies'                 
            }
        }
        stage('IQ Policy Evaluation') {
            steps {
                sh 'java -version' 
                nexusPolicyEvaluation failBuildOnNetworkError: false, 
                    iqApplication: 'depviz', 
                    iqStage: 'build',                     
                    iqScanPatterns: [
                        [scanPattern: '**/*.jar']
                    ],
                    callflow: [
                        enable: true,
                        entrypointStrategy: [
                          $class: 'NamedStrategy',
                          name: 'JAVA_MAIN',
                          namespaces: [
                            'ca.objectscape.depviz'
                          ]
                        ]
                    ]
            }
        }
    }
    
    post {
        always {
            deleteDir()
        }
    }
}
