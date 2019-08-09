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
        git url: 'https://github.com/eclipse/dartboard.git',
            branch: 'test'
      }
    }
    stage('Build Dartboard') {
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
    stage('Deploy') {
      when {
        branch 'master'
      }
      steps {
        container('jnlp') {
          sshagent (['projects-storage.eclipse.org-bot-ssh']) {
            sh 'cp -r org.eclipse.dartboard.update/target/repository org.eclipse.dartboard.update/target/release'
            sh 'ssh genie.dartboard@projects-storage.eclipse.org rm -rf /home/data/httpd/download.eclipse.org/dartboard/release'
            sh 'scp -r org.eclipse.dartboard.update/target/release genie.dartboard@projects-storage.eclipse.org:/home/data/httpd/download.eclipse.org/dartboard/'
          }
        }
      }
    }
  }
}
