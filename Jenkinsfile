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
    NEXUS_USER = credentials('nexus-user')
    NEXUS_PASSWORD = credentials('nexus-password')
    ID_RSA_KEY = credentials('id-rsa-jenkins')
  }


  options {
    buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '5', daysToKeepStr: '', numToKeepStr: '5')
  }

  stages {
    stage('Load GPG Key for Signing') {
      steps {
        sh '''
          GIT_COMMIT="$(git log -1 --oneline | cut -d' ' -f1)"
          gpg --version
          gpg --homedir /tmp --batch --import $GPG_SECRET
          gpg --homedir /tmp --import-ownertrust $GPG_OWNERTRUST
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
            sh '''
                mvn test verify -Dmaven.local.skip=true -Dmaven.remote.skip=false -Dmaven.main.skip=true -Dgpg.passphrase=$GPG_PASSPHRASE
            '''
        }
        post {
            always {
                junit '**/target/surefire-reports/*.xml'
                archive 'target/*.jar'
            }
        }
    }

    stage('Deploy Snapshot') {
        steps {
            sh '''
                GIT_SSH_COMMAND = "ssh -i $ID_RSA_KEY
                mvn -DskipTests -Dmaven.javadoc.skip=true -Dmaven.local.skip=true -Dmaven.remote.skip=false -Dgpg.passphrase=$GPG_PASSPHRASE deploy -P release
            '''
        }
        when {
            branch comparator: 'EQUALS', pattern: 'main'
        }
    }

    stage('Deploy Release') {
            steps {
                input message: 'Proceed with Release Deployment to Maven?', submitter: 'wolginm'
                sh '''
                    GIT_SSH_COMMAND = "ssh -i $ID_RSA_KEY
                    mvn release:clean release:prepare -s jenkins-settings.xml
                    mvn -DskipTests -Dmaven.javadoc.skip=true -Dmaven.local.skip=true -Dmaven.remote.skip=false release:perform -P release -s jenkins-settings.xml
                '''
            }
            when {
                branch comparator: 'CONTAINS', pattern: 'release'
                beforeOptions true
            }
        }

  }
}