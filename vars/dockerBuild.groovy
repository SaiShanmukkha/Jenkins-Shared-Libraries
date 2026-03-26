/**
 * dockerBuild.groovy
 *
 * Builds and optionally pushes a Docker image.
 *
 * Usage:
 *   dockerBuild(imageName: 'myapp')
 *   dockerBuild(
 *       imageName: 'myapp',
 *       imageTag: '1.0.0',
 *       dockerfile: 'Dockerfile',
 *       registry: 'docker.io/myorg',
 *       credentialsId: 'docker-hub-credentials',
 *       push: true
 *   )
 */

def call(Map config = [:]) {
    String imageName     = config.imageName     ?: error('dockerBuild: imageName is required')
    String imageTag      = config.imageTag      ?: 'latest'
    String dockerfile    = config.dockerfile    ?: 'Dockerfile'
    String registry      = config.registry      ?: ''
    String credentialsId = config.credentialsId ?: ''
    boolean push         = config.containsKey('push') ? config.push : false

    String fullImageName = registry ? "${registry}/${imageName}:${imageTag}" : "${imageName}:${imageTag}"

    log.info("Building Docker image: ${fullImageName}")
    sh "docker build -f ${dockerfile} -t ${fullImageName} ."

    if (push) {
        log.info("Pushing Docker image: ${fullImageName}")
        if (credentialsId) {
            docker.withRegistry(registry ? "https://${registry}" : '', credentialsId) {
                docker.image(fullImageName).push()
            }
        } else {
            sh "docker push ${fullImageName}"
        }
        log.info("Docker push complete")
    }
}
