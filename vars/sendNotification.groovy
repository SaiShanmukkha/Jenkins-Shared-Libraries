/**
 * sendNotification.groovy
 *
 * Sends a build status notification to Slack or email.
 *
 * Usage:
 *   sendNotification(channel: '#builds')
 *   sendNotification(type: 'email', recipients: 'team@example.com')
 */

def call(Map config = [:]) {
    String type       = config.type    ?: 'slack'
    String status     = currentBuild.currentResult ?: 'UNKNOWN'
    String jobName    = env.JOB_NAME   ?: 'Unknown Job'
    String buildNum   = env.BUILD_NUMBER ?: '0'
    String buildUrl   = env.BUILD_URL   ?: ''

    String message = "${status}: Job '${jobName}' [#${buildNum}] - ${buildUrl}"

    if (type == 'slack') {
        String channel       = config.channel       ?: '#general'
        String credentialsId = config.credentialsId ?: 'slack-token'
        String color = (status == 'SUCCESS') ? 'good' : (status == 'UNSTABLE') ? 'warning' : 'danger'

        log.info("Sending Slack notification to ${channel}")
        slackSend(
            channel: channel,
            color: color,
            message: message,
            tokenCredentialId: credentialsId
        )
    } else if (type == 'email') {
        String recipients = config.recipients ?: error('sendNotification: recipients is required for email')
        String subject    = "${status}: ${jobName} [#${buildNum}]"

        log.info("Sending email notification to ${recipients}")
        mail(
            to: recipients,
            subject: subject,
            body: message
        )
    } else {
        log.warn("sendNotification: unknown type '${type}'. Supported: slack, email")
    }
}
