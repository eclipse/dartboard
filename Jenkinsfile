pipeline {
	agent {
		kubernetes {
			label 'ui-test'
		}
	}
	tools {
		maven 'apache-maven-latest'
		jdk 'adoptopenjdk-hotspot-jdk8-latest'
	}
	stages {
		stage('Build') {
			steps {
				wrap([$class: 'Xvnc', takeScreenshot: false, useXauthority: true]) {
					sh 'mvn clean verify'
				}
			}
		}
	}
}