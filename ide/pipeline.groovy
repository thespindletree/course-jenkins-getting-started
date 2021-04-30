/* groovylint-disable-next-line CompileStatic */
pipeline {
    agent any
    triggers { pollSCM('* * * * *') }
    stages {
        stage('Checkout') {
            steps {
                // Get some code from a GitHub repository
                git url:'https://github.com/thespindletree/jgsu-spring-petclinic.git', branch: 'main'
            }
        }

        stage('Build') {
            steps {
                // Run Maven on a Unix agent.
                sh './mvnw clean package'
            }

            post {
                //If Maven was able to run the tests, even if some of the test
                //failed, record the test results and archive the jar file.
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                //}
                //changed {
                    emailext subject: "Job \'${JOB_NAME}\' (build ${BUILD_NUMBER}) ${currentBuild.result}",
                        body: "Please go to ${BUILD_URL} and verify the build",
                        attachLog: true,
                        compressLog: true,
                        to: "test@jenkins",
                        recipientProviders: [upstreamDevelopers(), requestor()]
                }
            }
        }
    }
}
