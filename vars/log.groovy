/**
 * log.groovy
 *
 * Provides simple logging utilities for Jenkins pipelines.
 *
 * Usage:
 *   log.info('This is an info message')
 *   log.warn('This is a warning')
 *   log.error('This is an error')
 */

def info(String message) {
    echo "[INFO] ${message}"
}

def warn(String message) {
    echo "[WARN] ${message}"
}

def error(String message) {
    echo "[ERROR] ${message}"
}
