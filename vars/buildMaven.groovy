/**
 * buildMaven.groovy
 *
 * Runs a Maven build with configurable goals and options.
 *
 * Usage:
 *   buildMaven()                                  // runs: mvn clean install
 *   buildMaven(goals: 'clean package -DskipTests')
 *   buildMaven(goals: 'clean verify', mavenOpts: '-Xmx1024m')
 */

def call(Map config = [:]) {
    String goals     = config.goals     ?: 'clean install'
    String mavenOpts = config.mavenOpts ?: ''
    String mavenHome = config.mavenHome ?: 'Maven'   // Jenkins tool name

    log.info("Running Maven: mvn ${goals}")

    withEnv(mavenOpts ? ["MAVEN_OPTS=${mavenOpts}"] : []) {
        withMaven(maven: mavenHome) {
            sh "mvn ${goals}"
        }
    }

    log.info("Maven build complete")
}
