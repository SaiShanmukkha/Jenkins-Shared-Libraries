package org.example

/**
 * PipelineUtils.groovy
 *
 * Utility class providing helper methods for Jenkins pipelines.
 *
 * Usage (in vars/ scripts or Jenkinsfiles):
 *   import org.example.PipelineUtils
 *   def utils = new PipelineUtils(this)
 *   utils.sh('echo hello')
 */
class PipelineUtils implements Serializable {

    private final def script

    PipelineUtils(def script) {
        this.script = script
    }

    /**
     * Returns true if the current build is running on the given branch.
     */
    boolean isOnBranch(String branchName) {
        return script.env.BRANCH_NAME == branchName
    }

    /**
     * Returns true if the current build was triggered by a pull request.
     */
    boolean isPullRequest() {
        return script.env.CHANGE_ID != null
    }

    /**
     * Reads a file from the workspace and returns its content as a String.
     */
    String readFileContent(String filePath) {
        return script.readFile(file: filePath).trim()
    }

    /**
     * Returns a map of common build information useful for notifications and
     * artifact naming.
     */
    Map buildInfo() {
        return [
            jobName    : script.env.JOB_NAME     ?: 'unknown',
            buildNumber: script.env.BUILD_NUMBER  ?: '0',
            buildUrl   : script.env.BUILD_URL     ?: '',
            branch     : script.env.BRANCH_NAME   ?: 'unknown',
            gitCommit  : script.env.GIT_COMMIT    ?: 'unknown'
        ]
    }
}
