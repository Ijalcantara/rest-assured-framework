Automation Framework Run Guide (with CI/CD)

This document explains how to execute the API Test Automation Framework locally and through the CI/CD pipeline using GitHub Actions.
The framework is built using:
    Java 17
    Rest Assured
    JUnit 5
    Maven
    Allure Reporting
    GitHub Actions (CI/CD)

1. Prerequisites
Ensure the following tools are installed before running the framework.

    Tool	Version
    Java	17 or higher
    Maven	3.8 or higher
    Git	Latest
    Node.js	Required for viewing CI/CD Allure report
    Allure CLI	Optional for local report viewing
    IDE	IntelliJ IDEA recommended

    Verify installation:
    java -version
    mvn -version
    git --version
    node -v


2. Clone the Repository
Clone the automation framework repository


3. Environment Configuration
The framework supports environment-based configuration.
    CI/CD Execution
    The GoRest API token is securely stored as a GitHub Secret. The workflow file sets the environment variables:
    
    env:
    ENV: qa
    GOREST_TOKEN: ${{ secrets.GOREST_TOKEN }}
    
    Environment variables used by the framework:
    
    Variable	Description
    ENV	Target test environment
    GOREST_TOKEN	GoRest API authentication token
    
    No manual export of the token is required for CI/CD execution.


4. Running Tests Locally
   Run the full test suite
        Terminal command: mvn clean test
   Run a specific test class
        Terminal command: mvn test -Dtest=GoRestUserCrudTests
   Run a specific test method
        Terminal command: mvn test -Dtest=GoRestUserCrudTests#testCreateUser


5. Generate Allure Reports Locally
   Generate report
    Terminal command: mvn allure:report
   View report
    Terminal command: allure serve target/allure-results

    The report includes:
        Test execution summary
        API request payload
        API response payload
        Execution steps
        Logs
    Note: It depends on the test case what is needed to be displayed in the allure-report (Optional fields: request payload, status code, response)

6. CI/CD Pipeline Execution
The project uses GitHub Actions to automatically run tests.
Workflow file location:
    .github/workflows/api-tests.yml

The pipeline triggers when code is pushed to any branch:
    git push origin feature/api-tests

7. CI/CD Workflow Steps
   The pipeline performs the following:
    Checkout repository code
    Setup Java 17 environment
    Cache Maven dependencies
    Validate the GOREST_TOKEN secret
    Execute API tests using Maven
    Generate Allure results
    Generate Allure HTML report
    Upload the report as a GitHub artifact

    Maven command used:
    
    mvn clean test verify \
        -Dallure.results.directory=target/allure-results \
        -Dmaven.test.failure.ignore=true
    
    The -Dmaven.test.failure.ignore=true option ensures Allure collects results even if some tests fail.

8. GitHub Secrets Configuration

    To configure the API token for CI/CD:
    Open the GitHub repository
    Go to Settings
    Select Secrets and Variables → Actions
    
    Add a new repository secret
    Secret name:
    GOREST_TOKEN
    
    The workflow automatically uses this secret.

9. Viewing CI/CD Test Reports

    After workflow completion, the Allure report is uploaded as a GitHub artifact.
    
    Step 1 — Download the Artifact
    Go to Actions in GitHub
    Select the latest workflow run
    Scroll to Artifacts
    Download allure-report
    
    Step 2 — Extract the Report
    Extract the artifact. Example folder structure:
    allure-report
    ├── index.html
    ├── data
    ├── widgets
    └── history
    
    Step 3 — Start a Local Server
    Open a terminal inside the extracted folder:

    npx serve .
    
    npx will install serve automatically if needed.
    
    Step 4 — Open in Browser
    The command will display a local URL, e.g.:
    Local: http://localhost:3000
    
    Open this URL to view the Allure Test Report, showing:
    
    Passed / failed tests
    Test execution details
    API request & response payloads
    Execution time
    Test steps

10. CI/CD Execution Flow
   
    Developer pushes code
        ↓
    GitHub Actions workflow triggered
        ↓
    Repository checkout
        ↓
    Java 17 environment setup
        ↓
    Token validation
        ↓
    API test execution
        ↓
    Allure results generation
        ↓
    Allure HTML report creation
        ↓
    Report uploaded as artifact