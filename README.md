# GreenKart Selenium Automation Framework

Production-grade Selenium + Cucumber + TestNG automation framework for [GreenKart](https://rahulshettyacademy.com/seleniumPractise/).

## Tech Stack
| Layer | Technology |
|---|---|
| Language | Java 11+ |
| Browser Automation | Selenium WebDriver 4.x |
| BDD | Cucumber 7 (TestNG runner) |
| Unit/Integration Tests | TestNG 7 |
| Driver Management | WebDriverManager |
| Reporting | ExtentReports 5 + Cucumber HTML |
| Logging | Log4j2 |
| Excel Data | Apache POI |
| Build | Maven 3.9+ |
| CI/CD | Jenkins (Declarative Pipeline) |

## Project Structure
```
greenkart-automation/
├── src/main/java/com/greenkart/
│   ├── base/          # BasePage (abstract), DriverManager
│   ├── interfaces/    # IPageActions, IWaitStrategy, IReportable
│   ├── pages/         # HomePage, CartPage, CheckoutPage, OrderConfirmationPage
│   ├── utils/         # ExcelUtils, DBUtils, ScreenshotUtils, ExtentReportManager
│   └── constants/     # Constants
├── src/test/java/com/greenkart/
│   ├── base/          # BaseTest (TestNG)
│   ├── hooks/         # Cucumber Hooks
│   ├── runners/       # TestRunner, ParallelTestRunner, SmokeTestRunner
│   ├── stepdefinitions/ # CommonSteps, HomePageSteps, CartSteps
│   ├── dataproviders/ # TestDataProvider (Excel + inline)
│   └── tests/         # HomePageTest, CartTest, E2ETest
├── src/test/resources/
│   ├── features/      # homepage.feature, cart.feature, checkout.feature
│   ├── testdata/      # testdata.xlsx
│   └── config/        # config.properties, staging/prod.properties, log4j2.xml
├── testng.xml          # Full suite
├── testng-smoke.xml    # Smoke only
├── testng-regression.xml
├── testng-parallel.xml # Parallel execution
├── Jenkinsfile         # CI/CD pipeline
└── pom.xml
```

## Running Tests

### Prerequisites
- Java 11+
- Maven 3.6+
- Chrome/Firefox/Edge browser installed

### Run full suite (default)
```bash
mvn clean test
```

### Run smoke tests only
```bash
mvn clean test -P smoke
```

### Run regression
```bash
mvn clean test -P regression
```

### Run in parallel
```bash
mvn clean test -P parallel
```

### Run in headless mode (CI)
```bash
mvn clean test -P ci
```

### Override parameters
```bash
mvn clean test \
  -Dbrowser=firefox \
  -Denv=prod \
  -Dheadless=true \
  -Dcucumber.filter.tags="@smoke"
```

## Reports
After execution, reports are generated in `test-output/`:
- **ExtentReports**: `test-output/reports/ExtentReport_*.html` — Dark theme with Pass/Fail/Skip filter
- **Cucumber HTML**: `test-output/cucumber-reports/cucumber-report.html`
- **Cucumber JSON**: `test-output/cucumber-reports/cucumber.json`
- **Screenshots**: `test-output/screenshots/`
- **Logs**: `test-output/logs/`

## Jenkins CI/CD
The Jenkinsfile provides a parameterized pipeline with:
- **BROWSER**: chrome / firefox / edge
- **ENV**: staging / prod
- **SUITE**: smoke / regression / parallel / all
- **HEADLESS**: true/false
- **TAGS**: Cucumber tag override

## OOP Design
- **Abstract Class**: `BasePage` — template for all page objects
- **Interfaces**: `IPageActions`, `IWaitStrategy`, `IReportable` — contracts
- **Inheritance**: All page classes extend `BasePage`
- **Polymorphism**: `BasePage` methods overridden per page need
- **Encapsulation**: WebElements private/protected with public methods
- **Abstraction**: `isPageLoaded()` abstract — each page defines its own check
