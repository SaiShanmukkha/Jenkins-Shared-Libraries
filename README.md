# Jenkins Shared Libraries

A collection of reusable Jenkins pipeline steps and utilities, implemented as a [Jenkins Shared Library](https://www.jenkins.io/doc/book/pipeline/shared-libraries/).

---

## Repository Structure

```
(root)
├── vars/                          # Global pipeline variables / steps
│   ├── log.groovy                 # Logging utilities
│   ├── log.txt                    # Help text for log variable
│   ├── gitCheckout.groovy         # Git checkout helper
│   ├── gitCheckout.txt
│   ├── buildMaven.groovy          # Maven build step
│   ├── buildMaven.txt
│   ├── dockerBuild.groovy         # Docker build & push step
│   ├── dockerBuild.txt
│   ├── sendNotification.groovy    # Slack / email notification step
│   └── sendNotification.txt
├── src/
│   └── org/example/
│       └── PipelineUtils.groovy   # Groovy utility class
└── resources/
    └── org/example/
        └── config.json            # Default configuration resource
```

---

## Setup

### 1. Configure the Shared Library in Jenkins

Go to **Manage Jenkins → Configure System → Global Pipeline Libraries** and add a new library:

| Field                  | Value                                              |
|------------------------|----------------------------------------------------|
| Name                   | `jenkins-shared-libraries`                        |
| Default version        | `main`                                             |
| Retrieval method       | *Modern SCM → GitHub*                              |
| Repository URL         | `https://github.com/SaiShanmukkha/Jenkins-Shared-Libraries` |

### 2. Import the Library in Your Jenkinsfile

```groovy
// Load the library implicitly (configured as auto-loaded) or explicitly:
@Library('jenkins-shared-libraries') _
```

---

## Available Steps

### `log`

Simple logging utilities.

```groovy
log.info('Build started')
log.warn('Disk usage is high')
log.error('Build failed')
```

---

### `gitCheckout`

Checks out a Git repository.

```groovy
gitCheckout(
    url: 'https://github.com/org/repo.git',
    branch: 'main',
    credentialsId: 'my-git-credentials'   // optional
)
```

| Parameter       | Required | Default | Description                              |
|-----------------|----------|---------|------------------------------------------|
| `url`           | ✅       | —       | Git repository URL                       |
| `branch`        | ❌       | `main`  | Branch to check out                      |
| `credentialsId` | ❌       | —       | Jenkins credentials ID for private repos |

---

### `buildMaven`

Runs a Maven build. Requires the **Pipeline Maven Integration** plugin.

```groovy
buildMaven(
    goals: 'clean package -DskipTests',
    mavenOpts: '-Xmx1024m',
    mavenHome: 'Maven-3.9'
)
```

| Parameter   | Required | Default         | Description                                   |
|-------------|----------|-----------------|-----------------------------------------------|
| `goals`     | ❌       | `clean install` | Maven goals and options                       |
| `mavenOpts` | ❌       | —               | JVM options (`MAVEN_OPTS`)                    |
| `mavenHome` | ❌       | `Maven`         | Jenkins-configured Maven installation name   |

---

### `dockerBuild`

Builds and optionally pushes a Docker image. Requires the **Docker Pipeline** plugin.

```groovy
dockerBuild(
    imageName: 'myapp',
    imageTag: "${env.BUILD_NUMBER}",
    registry: 'docker.io/myorg',
    credentialsId: 'docker-hub-credentials',
    push: true
)
```

| Parameter       | Required | Default      | Description                              |
|-----------------|----------|--------------|------------------------------------------|
| `imageName`     | ✅       | —            | Docker image name                        |
| `imageTag`      | ❌       | `latest`     | Image tag                                |
| `dockerfile`    | ❌       | `Dockerfile` | Path to the Dockerfile                   |
| `registry`      | ❌       | —            | Container registry (e.g. `docker.io/org`)|
| `credentialsId` | ❌       | —            | Jenkins credentials ID for registry      |
| `push`          | ❌       | `false`      | Push image after build                   |

---

### `sendNotification`

Sends a build notification to **Slack** or via **email**.

```groovy
// Slack (default)
sendNotification(channel: '#ci-builds')

// Email
sendNotification(type: 'email', recipients: 'team@example.com')
```

| Parameter       | Required        | Default    | Description                              |
|-----------------|-----------------|------------|------------------------------------------|
| `type`          | ❌              | `slack`    | `slack` or `email`                       |
| `channel`       | ❌ (Slack only) | `#general` | Slack channel name                       |
| `credentialsId` | ❌ (Slack only) | `slack-token` | Jenkins credentials ID for Slack token|
| `recipients`    | ✅ (Email only) | —          | Comma-separated email addresses          |

---

## Example Jenkinsfile

```groovy
@Library('jenkins-shared-libraries') _

pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                gitCheckout(
                    url: 'https://github.com/org/repo.git',
                    branch: 'main',
                    credentialsId: 'github-credentials'
                )
            }
        }

        stage('Build') {
            steps {
                buildMaven(goals: 'clean package -DskipTests')
            }
        }

        stage('Docker Build & Push') {
            steps {
                dockerBuild(
                    imageName: 'myapp',
                    imageTag: "${env.BUILD_NUMBER}",
                    registry: 'docker.io/myorg',
                    credentialsId: 'docker-hub-credentials',
                    push: true
                )
            }
        }
    }

    post {
        always {
            sendNotification(channel: '#ci-builds')
        }
    }
}
```

---

## Utility Class

### `org.example.PipelineUtils`

A Groovy helper class providing common pipeline utilities.

```groovy
import org.example.PipelineUtils

def utils = new PipelineUtils(this)

if (utils.isOnBranch('main')) {
    echo "Running on main branch"
}

def info = utils.buildInfo()
echo "Build #${info.buildNumber} on branch ${info.branch}"
```

---

## Requirements

| Plugin                              | Required by           |
|-------------------------------------|-----------------------|
| Pipeline Maven Integration Plugin   | `buildMaven`          |
| Docker Pipeline Plugin              | `dockerBuild`         |
| Slack Notification Plugin           | `sendNotification` (Slack) |
| Email Extension Plugin              | `sendNotification` (Email) |

---

## License

This project is open-source and available under the [MIT License](LICENSE).