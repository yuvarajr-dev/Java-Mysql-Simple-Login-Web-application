def call(mylibrary) {
try{
node('master') {
    stage('pull') {
        checkout([$class: 'GitSCM', branches: [[name: params.branch ]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: params.giturl ]]])
    }
    stage('artifact details') {
        tool name: 'java', type: 'jdk'
            tool name: 'maven', type: 'maven'
            def mvnHome = tool 'maven'
            env.PATH = "${mvnHome}/bin:${env.PATH}"
        rtMavenDeployer (
          id: 'deployer-unique-id',
          serverId: params.artifactid,
          releaseRepo: 'example-repo-local/${BUILD_NUMBER}',
          snapshotRepo: "example-repo-local/${BUILD_NUMBER}"
        )

    }
    stage('sonar'){
        // def scannerHome = tool 'Sonar';
        withSonarQubeEnv(params.sonarid) {
         sh 'mvn clean install sonar:sonar'
       }
    }
     stage("Quality Gate"){
      timeout(time: 60, unit: 'SECONDS') { // Just in case something goes wrong, pipeline will be killed after a timeout
      def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
      if (qg.status != 'OK') {
        error "Pipeline aborted due to quality gate failure: ${qg.status}"
    }
  }

 }
   
    stage('maven build'){

          rtMavenRun (
          tool: 'maven',
          type: 'maven',
          pom: 'pom.xml',
          goals: 'clean install',
          opts: '-Xms1024m -Xmx4096m',
          //resolverId: 'resolver-unique-id',
          deployerId: 'deployer-unique-id',
          )

    }
    stage('publish the artifact'){

          rtPublishBuildInfo (
          serverId: params.artifactid
          )

    }
        stage('Download artifact'){
        rtDownload (
           serverId: params.artifactid ,
           spec:
             """{
           "files": [
            {
              "pattern": "snapshot/${BUILD_NUMBER}/com/javawebtutor/LoginWebApp/1.0-SNAPSHOT/LoginWebApp-*.war",
              "target": "/var/lib/jenkins/workspace/${JOB_NAME}/"
            }
         ]
         }"""
        )
     }
    stage('Copy'){
        sh 'mv /var/lib/jenkins/workspace/${JOB_NAME}/${BUILD_NUMBER}/com/javawebtutor/LoginWebApp/1.0-SNAPSHOT/*.war  /var/lib/jenkins/workspace/${JOB_NAME}/${BUILD_NUMBER}/com/javawebtutor/LoginWebApp/1.0-SNAPSHOT/loginwebapp.war'

        sh 'scp var/lib/jenkins/workspace/${JOB_NAME}/${BUILD_NUMBER}/com/javawebtutor/LoginWebApp/1.0-SNAPSHOT/*.war tomcat@'+params.destip+':/opt/apache-tomcat-7.0.94/webapps/'
	
    }
    }
        }
    catch (err) {
      mail body:"${err}", subject: 'Build Failed', to: params.email
      currentBuild.result = 'FAILURE'
      }

}
