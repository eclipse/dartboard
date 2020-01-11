pipeline {
  agent {
    kubernetes {
      label 'dartboard-ui-tests'
      defaultContainer 'environment'
      yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: environment
    image: jonashungershausen/dartboard-ui-tests:latest
    tty: true
    command: [ "uid_entrypoint", "cat" ]
    resources:
      requests:
        memory: "2.6Gi"
        cpu: "1.3"
      limits:
        memory: "2.6Gi"
        cpu: "1.3"
  - name: jnlp
    image: 'eclipsecbi/jenkins-jnlp-agent'
    volumeMounts:
    - mountPath: /home/jenkins/.ssh
      name: volume-known-hosts
  volumes:
  - configMap:
      name: known-hosts
    name: volume-known-hosts
"""
    }
  }
  options {
    timeout(time: 30, unit: 'MINUTES')
    buildDiscarder(logRotator(numToKeepStr:'10'))
  }
  environment {
    MAVEN_OPTS="-Xms256m -Xmx2048m"
  }
  stages {
    stage('Prepare') {
      steps {
        git branch: 'jenkins', url: 'https://github.com/eclipse/dartboard.git'
      }
    }
    stage('Build and test Dartboard') {
      steps {
        wrap([$class: 'Xvnc', useXauthority: true]) {
          sh 'mvn clean verify'
        }
      }
      post {
        always {
          junit '*/target/surefire-reports/TEST-*.xml' 
        }
      }
    }
  }
  post {
    success {
      sh 'cp -r org.eclipse.dartboard.update/target/repository org.eclipse.dartboard.update/target/nightly'
      sshagent (credentials: ['genie.dartboard']) {
        sh 'ssh genie.dartboard@projects-storage.eclipse.org rm -rf /home/data/httpd/download.eclipse.org/dartboard/jenkins-test'
        sh 'scp -r org.eclipse.dartboard.update/target/nightly/ genie.dartboard@projects-storage.eclipse.org:/home/data/httpd/download.eclipse.org/dartboard/jenkins-test'
      }
    }
    failure {
      mail to: 'jonas.hungershausen@vogella.com',
           subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
           body: "Something is wrong with ${env.BUILD_URL}"
    }
  }
}
