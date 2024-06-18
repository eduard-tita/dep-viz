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
                sh 'mvn -B -DskipTests clean package'                 
            }
        }
        stage('IQ Policy Evaluation') {
            steps {
                sh 'java -version' 
                nexusPolicyEvaluation failBuildOnNetworkError: false, 
                    iqApplication: 'depviz', 
                    iqStage: 'build',                     
                    iqScanPatterns: [
                        [scanPattern: '**/dep-viz-*.jar']
                    ],
                    callflow: [
                        enable: true,
                        algorithm: 'RTA_PLUS',
                        properties: [
                          'bomxray.scan.workspace-resolve.threads': 4
                        ],
                        java: [
                          tool: 'Java 11',
                          options: [
                            '-Xmx4G'
                          ],
                        ],
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
