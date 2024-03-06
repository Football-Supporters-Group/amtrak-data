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
    SCM_USER = credentials('scm-user')
    SCM_PASSWORD = credentials('scm-password')
    ID_RSA_KEY = credentials('ida-rsa-key')
  }


  options {
    buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '5', daysToKeepStr: '', numToKeepStr: '5')
  }

  stages {
    stage("Check Preconditions") {
        when {
            expression {
                result = sh (script: "git log -1 | grep '.*\\[maven-release-plugin\\].*'", returnStatus: true) // Check if commit message contains skip ci label
                result == 0 // Evaluate the result
            }
        }
        steps {
            script {
                echo 'Got maven-release-plugin, aborting build' // Just an info message
                currentBuild.result = 'SUCCESS' // Mark the current build as aborted
                currentBuild.rawBuild.@result = hudson.model.Result.SUCCESS
                error('Skip-CI - maven-release-plugin') // Here you actually stop the build
            }
        }
    }
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
    stage('Prep Git for use.') {
        steps {
            sh '''
                git config --global user.email "junkwolginmark@gmail.com"
                git config --global user.name "${SCM_USER}"
                git config --add --local core.sshCommand "ssh -i ${ID_RSA_KEY}"
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
                mvn test verify -Dmaven.local.skip=true -Dmaven.remote.skip=false -Dmaven.main.skip=true
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
                mvn -DskipTests -Dmaven.javadoc.skip=true -Dmaven.local.skip=true -Dmaven.remote.skip=false -Dgpg.passphrase=$GPG_PASSPHRASE deploy -P release -s jenkins-settings.xml
            '''
        }
        when {
            branch comparator: 'EQUALS', pattern: 'main'
        }
    }

    stage('Deploy Release') {
        steps {
//                 input message: 'Proceed with Release Deployment to Maven?', submitter: 'wolginm'
                sh '''
                    mvn release:clean release:prepare -s jenkins-settings.xml
                    mvn --batch-mode -DskipTests -Dmaven.javadoc.skip=true -Dmaven.local.skip=true -Dmaven.remote.skip=false release:perform -P release -s jenkins-settings.xml
                '''
            }
//             when {
//                 branch comparator: 'CONTAINS', pattern: 'release'
//                 beforeOptions true
//             }
        }

  }
}