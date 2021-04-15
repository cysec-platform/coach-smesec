pipeline {
  agent any
  options {
    buildDiscarder(logRotator(numToKeepStr:'50'))
    disableConcurrentBuilds()
    timeout(time: 30, unit: 'MINUTES')
  }
  stages {
    stage ('Initialize') {
      steps {
        script {
          checkout scm
        }
      }
    }
    stage ('Compile') {
      steps {
        sh 'mvn clean compile'
      }
    }
    stage ('Test') {
      steps {
        sh 'mvn test'
      }
    }
    stage ('Package') {
      steps {
        sh 'mvn package -DskipTests'
        junit testResults: '**/target/surefire-reports/*.xml', skipPublishingChecks: true
      }
    }
    /*stage('SonarQube analysis') {
      steps {
        //withSonarQubeEnv('SonarQube') {
        withSonarQubeEnv('localhost sonarQube') {
          sh "mvn sonar:sonar"
        }
        sh '/bin/true'
      }
    }*/
  }
  post {
    /*always {
      publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'target/surefire-reports', reportFiles: 'index.html', reportName: 'SMESEC platform Report', reportTitles: 'smesec-platform'])
    }*/
    success {
      archiveArtifacts artifacts: '*/target/*.jar,*/target/*.xml', fingerprint: true
      //updateGitlabCommitStatus(name: 'build', state: 'success')
    }
    /*failure {
      updateGitlabCommitStatus(name: 'build', state: 'failed')
    }*/
  }
}
