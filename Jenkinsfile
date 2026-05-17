pipeline {
    agent any

    // ── Pipeline Parameters ────────────────────────────────────────────────────
    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Browser to run tests on'
        )
        choice(
            name: 'ENV',
            choices: ['staging', 'prod'],
            description: 'Target environment'
        )
        choice(
            name: 'SUITE',
            choices: ['smoke', 'regression', 'parallel', 'all'],
            description: 'Test suite to run'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browser in headless mode'
        )
        string(
            name: 'TAGS',
            defaultValue: '',
            description: 'Cucumber tags override (e.g. @smoke, @e2e). Leave blank to use suite default.'
        )
        string(
            name: 'THREAD_COUNT',
            defaultValue: '2',
            description: 'Number of parallel threads (used for parallel suite)'
        )
    }

    // ── Environment Variables ─────────────────────────────────────────────────
    environment {
        REPORTS_DIR  = 'test-output/reports'
        CUCUMBER_DIR = 'test-output/cucumber-reports'
    }

    // ── Options ───────────────────────────────────────────────────────────────
    options {
        timeout(time: 60, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Checking out source code..."
                checkout scm
            }
        }

        stage('Validate & Compile') {
            steps {
                echo "Compiling project..."
                bat "mvn clean compile test-compile -q"
            }
        }

        stage('Select Suite Config') {
            steps {
                script {
                    env.TESTNG_SUITE = params.SUITE == 'parallel'   ? 'testng-parallel.xml'   :
                                       params.SUITE == 'smoke'      ? 'testng-smoke.xml'      :
                                       params.SUITE == 'regression' ? 'testng-regression.xml' :
                                       'testng.xml'

                    env.CUCUMBER_TAGS = params.TAGS?.trim() ?
                        "-Dcucumber.filter.tags=\"${params.TAGS}\"" : ''

                    echo "Suite file  : ${env.TESTNG_SUITE}"
                    echo "Browser     : ${params.BROWSER}"
                    echo "Environment : ${params.ENV}"
                    echo "Headless    : ${params.HEADLESS}"
                    echo "Tags        : ${params.TAGS ?: '(default for suite)'}"
                }
            }
        }

        stage('Run Tests') {
            steps {
                echo "Starting test execution..."
                script {
                    def tagsArg = params.TAGS?.trim() ? "-Dcucumber.filter.tags=\"${params.TAGS}\"" : ''
                    bat """mvn test -Dtestng.suite.file=${env.TESTNG_SUITE} -Dbrowser=${params.BROWSER} -Denv=${params.ENV} -Dheadless=${params.HEADLESS} ${tagsArg} -Dfile.encoding=UTF-8"""
                }
            }
        }
    }

    // ── Post-build Actions ────────────────────────────────────────────────────
    post {
        always {
            echo "Archiving test reports..."

            publishHTML([
                allowMissing         : true,
                alwaysLinkToLastBuild: true,
                keepAll              : true,
                reportDir            : "${env.CUCUMBER_DIR}",
                reportFiles          : 'cucumber-report.html',
                reportName           : 'Cucumber HTML Report'
            ])

            publishHTML([
                allowMissing         : true,
                alwaysLinkToLastBuild: true,
                keepAll              : true,
                reportDir            : "${env.REPORTS_DIR}",
                reportFiles          : '*.html',
                reportName           : 'Extent Report'
            ])

            archiveArtifacts(
                artifacts: 'test-output/**/*',
                allowEmptyArchive: true,
                fingerprint: true
            )
        }

        success {
            echo "All tests PASSED! Build #${env.BUILD_NUMBER} - ${params.SUITE} suite on ${params.BROWSER}"
        }

        failure {
            echo "Tests FAILED! Build #${env.BUILD_NUMBER} - check console output for details"
        }

        unstable {
            echo "Build UNSTABLE - some tests may have failed"
        }
    }
}
