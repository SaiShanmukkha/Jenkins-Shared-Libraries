/**
 * gitCheckout.groovy
 *
 * Checks out a Git repository with configurable options.
 *
 * Usage:
 *   gitCheckout(
 *       url: 'https://github.com/org/repo.git',
 *       branch: 'main',
 *       credentialsId: 'my-git-credentials'   // optional
 *   )
 */

def call(Map config = [:]) {
    String url           = config.url           ?: error('gitCheckout: url is required')
    String branch        = config.branch        ?: 'main'
    String credentialsId = config.credentialsId ?: ''

    log.info("Checking out branch '${branch}' from ${url}")

    if (credentialsId) {
        checkout([
            $class: 'GitSCM',
            branches: [[name: "*/${branch}"]],
            userRemoteConfigs: [[
                url: url,
                credentialsId: credentialsId
            ]]
        ])
    } else {
        checkout([
            $class: 'GitSCM',
            branches: [[name: "*/${branch}"]],
            userRemoteConfigs: [[url: url]]
        ])
    }

    log.info("Checkout complete")
}
