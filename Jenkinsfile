#!groovy

pipeline {

  agent any
  tools {
      maven 'maven-3.9'
      jdk 'jdk17'
  }

  environment {
    GPG_SECRET = credentials('gpg-secret')
    GPG_SECRET_NAME = credentials('gpg-secret-name')
    GPG_OWNERTRUST = credentials('gpg-ownertrust')
    GPG_PASSPHRASE = credentials('gpg-passphrase')
  }


  options {
    buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '5', daysToKeepStr: '', numToKeepStr: '5')
  }

  stages {
    stage('Load GPG Key for Signing') {
      steps {
        sh '''
          GIT_COMMIT="$(git log -1 --oneline | cut -d' ' -f1)"
          gpg --homedir /tmp --batch --import $GPG_SECRET
          gpg ---homedir /tmp --import-ownertrust $GPG_OWNERTRUST
          gpg --homedir /tmp --list-keys
        '''
      }
    }
    stage('Pre-Build') {
      steps {
        sh '''
          java -version
        '''
      }
    }
    stage('Build') {
        steps {
            sh 'mvn -B -DskipTests -Dmaven.javadoc.skip=true clean package'
        }
    }
    stage('Test') {
        steps {
            sh 'mvn test verify'
        }
        post {
            always {
                junit '**/target/surefire-reports/*.xml'
                archive 'target/*.jar'
            }
        }
    }

    stage('Clean Up GPG Key for Signing') {
      steps {
        sh '''
          echo test
          '''
      }
    }

    stage('Deploy') {
        steps {
            input message: 'Procede with Deployment to Maven?', submitter: 'wolginm'
            sh 'mvn -DskipTests -Dmaven.javadoc.skip=true deploy'
        }
        when {
            branch comparator: 'EQUALS', pattern: 'main'
            beforeOptions true
        }
    }

  }
}