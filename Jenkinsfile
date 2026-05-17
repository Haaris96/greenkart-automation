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
        JAVA_HOME    = tool 'JDK-21'       // Configure JDK in Jenkins → Global Tool Configuration
        MAVEN_HOME   = tool 'Maven-3.9'    // Configure Maven in Jenkins
        PATH         = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${env.PATH}"
        REPORTS_DIR  = 'test-output/reports'
        CUCUMBER_DIR = 'test-output/cucumber-reports'
    }

    // ── Options ───────────────────────────────────────────────────────────────
    options {
        timeout(time: 60, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        ansiColor('xterm')
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
                sh "mvn clean compile test-compile -q"
            }
        }

        stage('Select Suite Config') {
            steps {
                script {
                    env.TESTNG_SUITE = params.SUITE == 'parallel'    ? 'testng-parallel.xml'  :
                                       params.SUITE == 'smoke'       ? 'testng-smoke.xml'     :
                                       params.SUITE == 'regression'  ? 'testng-regression.xml':
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
                sh """
                    mvn test \
                        -Dtestng.suite.file=${env.TESTNG_SUITE} \
                        -Dbrowser=${params.BROWSER} \
                        -Denv=${params.ENV} \
                        -Dheadless=${params.HEADLESS} \
                        ${env.CUCUMBER_TAGS} \
                        -Dfile.encoding=UTF-8 \
                        -q
                """
            }
            post {
                always {
                    echo "Test execution stage complete"
                }
            }
        }
    }

    // ── Post-build Actions ────────────────────────────────────────────────────
    post {
        always {
            echo "Archiving test reports..."

            // Publish TestNG results
            testNG(
                reportFilenamePattern: 'test-output/testng-results.xml',
                failureOnFailedTestConfig: false
            )

            // Publish Cucumber HTML report
            publishHTML([
                allowMissing         : true,
                alwaysLinkToLastBuild: true,
                keepAll              : true,
                reportDir            : "${env.CUCUMBER_DIR}",
                reportFiles          : 'cucumber-report.html',
                reportName           : 'Cucumber HTML Report'
            ])

            // Publish ExtentReports
            publishHTML([
                allowMissing         : true,
                alwaysLinkToLastBuild: true,
                keepAll              : true,
                reportDir            : "${env.REPORTS_DIR}",
                reportFiles          : '*.html',
                reportName           : 'Extent Report'
            ])

            // Archive screenshots and logs
            archiveArtifacts(
                artifacts: 'test-output/**/*',
                allowEmptyArchive: true,
                fingerprint: true
            )
        }

        success {
            echo "All tests PASSED!"
            emailext(
                subject: "✅ [GreenKart] Tests PASSED - ${params.SUITE} | ${params.BROWSER} | Build #${env.BUILD_NUMBER}",
                body: """
                <h2>Test Execution Successful</h2>
                <p><b>Build:</b> #${env.BUILD_NUMBER}</p>
                <p><b>Suite:</b> ${params.SUITE}</p>
                <p><b>Browser:</b> ${params.BROWSER}</p>
                <p><b>Environment:</b> ${params.ENV}</p>
                <p><b>Headless:</b> ${params.HEADLESS}</p>
                <p><a href="${env.BUILD_URL}">View Build</a></p>
                """,
                to: '${DEFAULT_RECIPIENTS}',
                mimeType: 'text/html'
            )
        }

        failure {
            echo "Some tests FAILED!"
            emailext(
                subject: "❌ [GreenKart] Tests FAILED - ${params.SUITE} | ${params.BROWSER} | Build #${env.BUILD_NUMBER}",
                body: """
                <h2>Test Execution Failed</h2>
                <p><b>Build:</b> #${env.BUILD_NUMBER}</p>
                <p><b>Suite:</b> ${params.SUITE}</p>
                <p><b>Browser:</b> ${params.BROWSER}</p>
                <p><b>Environment:</b> ${params.ENV}</p>
                <p><a href="${env.BUILD_URL}console">View Console</a></p>
                """,
                to: '${DEFAULT_RECIPIENTS}',
                mimeType: 'text/html'
            )
        }

        unstable {
            echo "Build is UNSTABLE - some tests may have failed"
        }

        cleanup {
            cleanWs(
                cleanWhenNotBuilt: false,
                deleteDirs: true,
                disableDeferredWipeout: true,
                notFailBuild: true,
                patterns: [
                    [pattern: 'test-output/**', type: 'INCLUDE']
                ]
            )
        }
    }
}
