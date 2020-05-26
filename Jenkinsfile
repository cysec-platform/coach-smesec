pipeline {
  agent any
  options {
    buildDiscarder(logRotator(numToKeepStr:'50'))
    disableConcurrentBuilds()
  }
  stages {
    stage ('Initialize') {
      steps {
		checkout scm: [
				$class: 'GitSCM',
				branches: scm.branches,
				doGenerateSubmoduleConfigurations: false,
				extensions: [[$class: 'SubmoduleOption',
							  disableSubmodules: false,
							  parentCredentials: false,
							  recursiveSubmodules: true,
							  reference: '',
							  trackingSubmodules: false]],
				submoduleCfg: [],
				userRemoteConfigs: scm.userRemoteConfigs
		]      
		sh 'mvn clean'
      }
    }
    stage ('Build') {
      steps {
        sh 'mvn -DskipTests compile'
      }
    }
    /*stage ('Test on JDK8') {
      agent {
        docker { 
         image 'maven:3.6.0-jdk-8-slim'
         reuseNode true
         }
      }
      options {
        timeout(time: 30, unit: 'MINUTES')
      }
      steps{
        sh 'mvn -DforkCount=0 test jacoco:report'
      }
      
    }*/
    
    stage ('Package all') {
      steps {
        sh 'mvn -DskipTests install'
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
      updateGitlabCommitStatus(name: 'build', state: 'success')
    }
    failure {
      updateGitlabCommitStatus(name: 'build', state: 'failed')
    }
  }
}