def call (def artifactoryServer, def mvnHome,def pom, def goal, def releaseRepo, def snapshotRepo){
  rtMavenDeployer (
      id: 'deployer-unique-id',
      serverId: "${artifactoryServer}",
      releaseRepo: "${releaseRepo}/${BUILD_NUMBER}",
      snapshotRepo: "${snapshotRepo}/${BUILD_NUMBER}"
  )

  rtMavenRun (
      tool: "${mvnHome}",
      pom: "${pom}",
      goals: "${goal}",
      opts: '-Xms1024m -Xmx4096m',
      deployerId: 'deployer-unique-id',
  )
}
