#!groovy
pipeline {

  agent any
  tools {
      maven 'maven-3.9'
      jdk 'jdk17'
      dockerTool 'docker-agent'
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
    SSH_PUBLIC_KEY = credentials('ssh-public-key')
  }


  options {
    buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '5', daysToKeepStr: '', numToKeepStr: '5')
  }

  parameters {
    booleanParam(defaultValue: true, description: 'Execute pipeline?', name: 'shouldBuild')
    booleanParam(defaultValue: false, description: 'Execute pipeline?', name: 'overrideBuild')
 }

  stages {
    stage("Check Preconditions") {
        when {
            expression {
                //https://stackoverflow.com/questions/43016942/can-a-jenkins-job-be-aborted-with-success-result
                result = sh (script: "git log -1 | grep '.*\\[maven-release-plugin\\].*'", returnStatus: true) // Check if commit message contains skip ci label
                result == 0 // Evaluate the result
            }
        }
        steps {
            script {
                    echo 'Got maven-release-plugin, aborting build' // Just an info message
                    currentBuild.result = 'SUCCESS' // Mark the current build as aborted
                    env.shouldBuild = "false"
                    env.shouldBuild = env.overrideBuild
                    //currentBuild.rawBuild.@result = hudson.model.Result.SUCCESS
                    //error('Skip-CI - maven-release-plugin') // Here you actually stop the build
            }
        }
    }
    stage('Load GPG Key for Signing') {
        when {
            expression {
                return env.shouldBuild != "false"
            }
        }
      steps {
        script {
            def artifactId=sh (script: 'mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout', returnStdout: true).trim()
            def groupId=sh (script: 'mvn help:evaluate -Dexpression=project.groupId -q -DforceStdout', returnStdout: true).trim()
            def version=sh (script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout', returnStdout: true).trim()
//             def REQUEST_GAV=sh (script:'(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)',returnStdout: true).trim()
            def REQUEST_GAV=artifactId+"-"+version
            echo REQUEST_GAV
//             def REQUEST_VERSION=sh script:'$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)',returnStdout: true).trim()
            def REQUEST_VERSION=version
            echo REQUEST_VERSION

//             def JAR_NAME=sh (script:'$(./mvnw help:evaluate -Dexpression=project.groupId -q -DforceStdout)/$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)',returnStdout: true).trim()
            def JAR_NAME=groupId + "/" + artifactId + ":" + env.BUILD_NUMBER
            echo JAR_NAME
        }
        sh '''
          GIT_COMMIT="$(git log -1 --oneline | cut -d' ' -f1)"
          gpg --version
          gpg --homedir /tmp --batch --import $GPG_SECRET
          gpg --homedir /tmp --import-ownertrust $GPG_OWNERTRUST
          gpg --homedir /tmp --list-keys
          if [ -f ~/.ssh/id_rsa.pub ]; then
             rm ~/.ssh/id_rsa.pub
          fi
          if [ -f ~/.ssh/id_rsa ]; then
             rm ~/.ssh/id_rsa
          fi
          cp $SSH_PUBLIC_KEY ~/.ssh/id_rsa.pub
          cp $ID_RSA_KEY ~/.ssh/id_rsa
          cat ~/.ssh/id_rsa.pub
          cat ~/.ssh/id_rsa
        '''
      }
    }
    stage('Prep Git for use.') {
        when {
            expression {
                return env.shouldBuild != "false"
            }
        }
        steps {
            sh '''
                git config --global user.email "junkwolginmark@gmail.com"
                git config --global user.name "${SCM_USER}"
                git config --add --local core.sshCommand "ssh -i ${ID_RSA_KEY}"
                '''
            }
    }

    stage('Pre-Build') {
    when {
        expression {
            return env.shouldBuild != "false"
        }
    }
      steps {
        sh '''
          java -version
        '''
      }
    }
    stage('Build') {
        when {
            expression {
                return env.shouldBuild != "false"
            }
        }
        steps {
            sh 'mvn -B -DskipTests -Dmaven.javadoc.skip=true clean package'
        }
    }
    stage('Test') {
        when {
            expression {
                return env.shouldBuild != "false"
            }
        }
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
        when {
            branch comparator: 'EQUALS', pattern: 'main'
            expression {
                return env.shouldBuild != "false"
            }
        }
        steps {
            sh '''
                mvn -DskipTests -Dmaven.javadoc.skip=true -Dmaven.local.skip=true -Dmaven.remote.skip=false -Dgpg.passphrase=$GPG_PASSPHRASE deploy -P release -s jenkins-settings.xml
            '''
        }
    }

    stage('Deploy Release') {
        when {
            branch comparator: 'GLOB', pattern: '**/release/*'
            beforeOptions true
            expression {
                return env.shouldBuild != "false"
            }
        }
        steps {
                input message: 'Proceed with Release Deployment to Maven?', submitter: 'wolginm'
                sh '''
                    mvn release:clean release:prepare -s jenkins-settings.xml
                    mvn --batch-mode -DskipTests -Dmaven.javadoc.skip=true release:perform -P release \
                        -s jenkins-settings.xml
                    '''
            }
        }
    stage('Build Docker Image') {
//        when {
//            branch comparator: 'GLOB', pattern: '**/release/*'
//            beforeOptions true
//            expression {
//                return env.shouldBuild != "false"
//            }
//        }
       steps {
           script {
                REQUEST_GAV=sh (returnStdout: true, script:'$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)').trim()
                REQUEST_VERSION=sh (returnStdout: true, script:'$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)').trim()
                JAR_NAME=sh (returnStdout: true, script:'$(./mvnw help:evaluate -Dexpression=project.groupId -q -DforceStdout)/$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)').trim()
                def image = docker.build '--build-arg request_gav=$REQUEST_GAV --build-arg request_version=$REQUEST_VERSION -t $JAR_NAME .'
           }

            sh '''
                docker build \
                    --build-arg request_gav=$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout) \
                    --build-arg request_version=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout) -t \
                    $(./mvnw help:evaluate -Dexpression=project.groupId -q -DforceStdout)/$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout) .
                '''
       }
    }
  }
}