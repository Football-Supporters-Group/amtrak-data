pipeline {

  agent any
  tools {
      maven 'maven-3.8'
  }


  options {
    buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '5', daysToKeepStr: '', numToKeepStr: '5')
  }

  stages {
    stage('Pre-Build') {
      steps {
        sh '''
          java -version
        '''
      }
    }
    stage('Build') {
        steps {
            sh 'mvn -B -DskipTests clean package'
        }
    }
    stage('Test') {
        steps {
            sh 'mvn test'
        }
        post {
            always {
                junit '**/target/surefire-reports/*.xml'
                archive 'target/*.jar'
            }
        }
    }

    stage('Deploy') {
        steps {
            input message: 'Procede with Deployment to Maven?', submitter: 'wolginm'
            sh 'mvn deploy'
        }
    }

  }
}