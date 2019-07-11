@Library('shared-library')_
node(label: 'master'){
    //Variables
    def gitURL = "https://github.com/sameer-shukur/Java-Mysql-Simple-Login-Web-application.git"
    def repoBranch = "master"
    def applicationName = "WebApp"
    def sonarqubeServer = "sonar"
    def sonarqubeGoal = "clean verify sonar:sonar"
    def mvnHome = "maven"
    def pom = "pom.xml"
    def goal = "clean install"
    def artifactory = "Artifactory"
    def releaseRepo = "libs-release-local"
    def snapshotRepo = "libs-snapshot-local"
    def lastSuccessfulBuildID = 0

    stage('Get Last Successful Build Number'){
        def build = currentBuild.previousBuild
        while (build != null) {
            if (build.result == "SUCCESS")
            {
                lastSuccessfulBuildID = build.id as Integer
                break
            }
            build = build.previousBuild
        }
        echo "${lastSuccessfulBuildID}"
    }
        stage('Git-Checkout'){
        gitClone "${gitURL}","${repoBranch}"    
    }
    
    //Sonarqube Analysis
    stage('Sonarqube-scan'){
        sonarqubeScan "${mvnHome}","${sonarqubeGoal}","${pom}", "${sonarqubeServer}"
    }
    
    //Quality-gate
    stage('Quality-Gate'){
        qualityGate "${sonarqubeServer}"
    }
    
    //MVN Build
    stage('Maven Build and Push to Artifactory'){
        mavenBuild "${artifactory}","${mvnHome}","${pom}", "${goal}", "${releaseRepo}", "${snapshotRepo}"
    }
}
