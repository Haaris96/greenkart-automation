# GreenKart Selenium Automation Framework

> Production-grade BDD + TestNG automation framework for [GreenKart](https://rahulshettyacademy.com/seleniumPractise/) — a veggie & fruit e-commerce practice site by Rahul Shetty Academy.

![Java](https://img.shields.io/badge/Java-21-orange?logo=java)
![Selenium](https://img.shields.io/badge/Selenium-4.15-green?logo=selenium)
![Cucumber](https://img.shields.io/badge/Cucumber-7.14-brightgreen?logo=cucumber)
![TestNG](https://img.shields.io/badge/TestNG-7.8-blue)
![Maven](https://img.shields.io/badge/Maven-3.9-red?logo=apachemaven)
![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-orange?logo=jenkins)
![ExtentReports](https://img.shields.io/badge/ExtentReports-5.1-purple)

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [OOP Design Principles](#oop-design-principles)
- [Project Structure](#project-structure)
- [Application Under Test](#application-under-test)
- [Test Coverage](#test-coverage)
- [Prerequisites](#prerequisites)
- [Running Tests Locally](#running-tests-locally)
- [Test Reports](#test-reports)
- [Data-Driven Testing](#data-driven-testing)
- [Jenkins CI/CD](#jenkins-cicd)
- [Parallel Execution](#parallel-execution)
- [Configuration](#configuration)

---

## Overview

This framework automates the complete GreenKart shopping workflow — from product search to order confirmation — using the **Page Object Model (POM)** pattern with full **BDD (Behavior-Driven Development)** support via Cucumber feature files.

**Key capabilities:**
- End-to-end test coverage: search → add to cart → checkout → coupon → country selection → order confirmation
- Thread-safe parallel execution via `ThreadLocal<WebDriver>`
- Data-driven testing with Excel (Apache POI) and TestNG `@DataProvider`
- Inline Cucumber step screenshots in Extent Reports
- Parameterized Jenkins pipeline (browser, environment, suite, tags)

---

## Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 21 |
| Browser Automation | Selenium WebDriver | 4.15.0 |
| BDD Framework | Cucumber | 7.14.0 |
| Test Runner | TestNG | 7.8.0 |
| Driver Management | WebDriverManager | 5.6.3 |
| Reporting | ExtentReports + Cucumber Adapter | 5.1.1 / 1.14.0 |
| Logging | Log4j2 | 2.21.1 |
| Excel Data | Apache POI | 5.2.4 |
| DI (Cucumber) | PicoContainer | 6.0.0 |
| Build Tool | Maven | 3.9.15 |
| CI/CD | Jenkins Declarative Pipeline | — |

---

## OOP Design Principles

| Principle | Implementation |
|---|---|
| **Abstraction** | `BasePage` abstract class with `isPageLoaded()` abstract method |
| **Encapsulation** | `@FindBy` WebElements are `private`; page actions exposed via `public` methods |
| **Inheritance** | `HomePage`, `CartPage`, `CheckoutPage`, `OrderConfirmationPage` all extend `BasePage` |
| **Polymorphism** | `isPageLoaded()` overridden differently in each page class |
| **Interfaces** | `IPageActions` (click, type, getText…), `IWaitStrategy` (explicit/fluent waits), `IReportable` |
| **Dependency Injection** | `ScenarioContext` injected via PicoContainer into all Cucumber step definition classes |

---

## Project Structure

```
greenkart-automation/
│
├── src/main/java/com/greenkart/
│   ├── base/
│   │   ├── BasePage.java           # Abstract base — implements IPageActions + IWaitStrategy
│   │   └── DriverManager.java      # ThreadLocal WebDriver (thread-safe parallel execution)
│   │
│   ├── interfaces/
│   │   ├── IPageActions.java       # click, type, getText, isDisplayed, hover, scroll...
│   │   ├── IWaitStrategy.java      # waitForVisibility, waitForClickability, fluentWait...
│   │   └── IReportable.java        # logInfo, logPass, logFail, attachScreenshot
│   │
│   ├── pages/
│   │   ├── HomePage.java           # Product search, add to cart, cart icon
│   │   ├── CartPage.java           # Cart preview panel (slide-in)
│   │   ├── CheckoutPage.java       # Checkout summary (#/cart) — coupon, place order
│   │   └── OrderConfirmationPage.java  # Order success confirmation (#/country)
│   │
│   ├── utils/
│   │   ├── ConfigReader.java       # Singleton properties loader (env-aware)
│   │   ├── ExtentReportManager.java # Thread-safe Extent Reports with Base64 screenshots
│   │   ├── ScreenshotUtils.java    # File + Base64 screenshot capture
│   │   ├── ExcelUtils.java         # Apache POI Excel read/write for data-driven tests
│   │   └── DBUtils.java            # MySQL JDBC utility (executeQuery, getSingleValue)
│   │
│   └── constants/
│       └── Constants.java          # URLs, timeouts, coupon codes, group names, file paths
│
├── src/test/java/com/greenkart/
│   ├── base/
│   │   └── BaseTest.java           # @BeforeMethod/@AfterMethod — driver init, reports
│   │
│   ├── hooks/
│   │   └── Hooks.java              # Cucumber @Before/@After/@AfterStep — screenshots per step
│   │
│   ├── runners/
│   │   ├── TestRunner.java         # Default runner (@smoke or @regression or @e2e)
│   │   ├── SmokeTestRunner.java    # @smoke tags only
│   │   └── ParallelTestRunner.java # Parallel DataProvider runner
│   │
│   ├── stepdefinitions/
│   │   ├── ScenarioContext.java    # PicoContainer DI — shares page objects across steps
│   │   ├── CommonSteps.java        # Shared steps: navigate, add to cart, proceed, place order
│   │   ├── HomePageSteps.java      # Search and product count assertions
│   │   └── CartSteps.java          # Cart panel management, coupon assertions
│   │
│   ├── dataproviders/
│   │   └── TestDataProvider.java   # @DataProvider: productData, couponData, orderData
│   │
│   └── tests/
│       ├── HomePageTest.java        # TestNG: search, add to cart, product listing
│       ├── CartTest.java            # TestNG: coupon apply, cart management
│       ├── E2ETest.java             # TestNG: full purchase flows
│       └── DiagnosticTest.java      # Dev utility — CSS selector discovery (not part of suite)
│
├── src/test/resources/
│   ├── features/
│   │   ├── homepage.feature        # Search, add-to-cart, scenario outlines
│   │   ├── cart.feature            # Coupon application, cart management
│   │   └── checkout.feature        # End-to-end purchase scenarios
│   │
│   ├── testdata/
│   │   └── testdata.xlsx           # Excel: Products, Users, Coupons, Orders sheets
│   │
│   └── config/
│       ├── config.properties       # Default configuration
│       ├── staging.properties      # Staging environment overrides
│       ├── prod.properties         # Production environment overrides
│       ├── extent.properties       # Extent Reports configuration
│       ├── spark-config.xml        # Extent Spark reporter theme settings
│       └── log4j2.xml              # Log4j2 logging configuration
│
├── testng.xml                      # Full suite (all tests)
├── testng-smoke.xml                # Smoke suite
├── testng-regression.xml           # Regression suite
├── testng-parallel.xml             # Parallel execution suite
├── Jenkinsfile                     # Parameterized declarative pipeline
└── pom.xml                         # Maven build + profiles (smoke, regression, parallel, ci)
```

---

## Application Under Test

**GreenKart** — https://rahulshettyacademy.com/seleniumPractise/

### Actual checkout flow (4 steps)

```
Home Page (product listing)
    ↓  Add products to cart
Cart Preview Panel (slide-in)
    ↓  Click PROCEED TO CHECKOUT
Checkout Summary Page (#/cart)
    ↓  Apply coupon (optional) → Click Place Order
Country Selection Page (#/country)
    ↓  Select India → Check T&C → Click Proceed
Order Confirmation (same page)
    ✅  "Thank you, your order has been placed successfully"
```

### Key selectors discovered

| Element | Selector |
|---|---|
| Product name | `h4.product-name` (text: "Brocolli - 1 Kg") |
| Add to Cart button | `//button[normalize-space(text())='ADD TO CART']` |
| Cart icon | `a.cart-icon` |
| Cart count | `a.cart-icon span.cart-count` |
| PROCEED TO CHECKOUT | `.action-block button` |
| Coupon input | `input.promoCode` |
| Apply coupon button | `button.promoBtn` |
| Discount % | `span.discountPerc` |
| Place Order button | `//button[normalize-space(text())='Place Order']` |
| Country dropdown | `select` (tagName) |
| T&C checkbox | `input.chkAgree` |
| Confirmation span | `span[style*='color:green']` |

---

## Test Coverage

### Cucumber Feature Files

| Feature | Scenarios | Tags |
|---|---|---|
| `homepage.feature` | 4 scenarios + 2 outlines | `@smoke @homepage @search @add-to-cart` |
| `cart.feature` | 4 scenarios + 1 outline | `@regression @cart @coupon @cart-management` |
| `checkout.feature` | 3 scenarios + 1 outline | `@regression @checkout @e2e @smoke` |

### TestNG Test Classes

| Class | Tests | Groups |
|---|---|---|
| `HomePageTest` | 7 | smoke, regression |
| `CartTest` | 6 | smoke, regression |
| `E2ETest` | 5 | regression, e2e |

### Test Results (local)

| Suite | Tests | Pass | Fail |
|---|---|---|---|
| Smoke | 29 | 29 | 0 |
| Regression | 61 | 61 | 0 |

---

## Prerequisites

- **Java 21** (or 11+)
- **Maven 3.9+**
- **Chrome / Firefox / Edge** browser installed
- **Internet access** to https://rahulshettyacademy.com/seleniumPractise/

WebDriverManager automatically downloads the matching ChromeDriver/GeckoDriver — no manual driver setup needed.

---

## Running Tests Locally

### Full suite (default)
```bash
mvn clean test
```

### Smoke tests only
```bash
mvn clean test -P smoke
```

### Regression suite
```bash
mvn clean test -P regression
```

### Parallel execution
```bash
mvn clean test -P parallel
```

### Headless (CI mode)
```bash
mvn clean test -P ci
```

### Custom parameters
```bash
mvn clean test \
  -Dbrowser=firefox \
  -Denv=staging \
  -Dheadless=true \
  -Dcucumber.filter.tags="@e2e"
```

### Run a specific TestNG class
```bash
mvn test -Dtest=CartTest -Dheadless=true
```

---

## Test Reports

Reports are generated in `test-output/` after every run:

| Report | Path | Description |
|---|---|---|
| **Extent Report** | `test-output/reports/ExtentReport_*.html` | Dark theme, pass/fail/skip filter, inline screenshots |
| **Cucumber HTML** | `test-output/cucumber-reports/cucumber-report.html` | Step-by-step BDD report with scenario status |
| **Cucumber JSON** | `test-output/cucumber-reports/cucumber.json` | Machine-readable for CI integration |
| **Screenshots** | `test-output/screenshots/` | Captured on failure + after every step (Cucumber) |
| **Logs** | `test-output/logs/` | Log4j2 rolling file logs |

---

## Data-Driven Testing

### Excel (Apache POI)

Test data lives in `src/test/resources/testdata/testdata.xlsx`:

| Sheet | Columns | Used In |
|---|---|---|
| `Products` | productName, category, expectedPrice | `HomePageTest`, `CartTest` |
| `Users` | username, password, email | Login tests |
| `Coupons` | couponCode, isValid, expectedDiscount | `CartTest.couponTestFromExcel` |
| `Orders` | product, quantity, coupon | `E2ETest.dataDrivenE2EPurchase` |

### TestNG DataProviders

```java
@Test(dataProvider = "productData", dataProviderClass = TestDataProvider.class)
public void addProductToCart(Map<String, String> data) { ... }

@Test(dataProvider = "couponData", dataProviderClass = TestDataProvider.class)
public void couponTestFromExcel(Map<String, String> data) { ... }
```

---

## Jenkins CI/CD

The `Jenkinsfile` defines a **parameterized declarative pipeline**:

### Parameters

| Parameter | Type | Options | Default |
|---|---|---|---|
| `BROWSER` | Choice | chrome, firefox, edge | chrome |
| `ENV` | Choice | staging, prod | staging |
| `SUITE` | Choice | smoke, regression, parallel, all | smoke |
| `HEADLESS` | Boolean | true / false | true |
| `TAGS` | String | any Cucumber tag | _(suite default)_ |
| `THREAD_COUNT` | String | number | 2 |

### Pipeline Stages

```
Checkout  →  Validate & Compile  →  Select Suite Config  →  Run Tests
```

### Post-build actions
- Publishes **Cucumber HTML Report** and **Extent Report** (HTML Publisher plugin)
- Archives `test-output/**/*` as build artifacts
- Prints pass/fail summary to console

### Running via Jenkins

1. Open [http://localhost:8080/job/greenkart-automation](http://localhost:8080/job/greenkart-automation)
2. Click **Build with Parameters**
3. Choose Browser, Environment, Suite, Headless mode
4. Click **Build**

---

## Parallel Execution

Configured via `testng-parallel.xml` and `ParallelTestRunner`:

```xml
<suite name="Parallel Suite" parallel="methods" thread-count="3">
```

- **Thread-safe WebDriver**: `DriverManager` uses `ThreadLocal<WebDriver>` — each thread gets its own browser instance
- **Implicit wait disabled** (set to 0) to prevent conflicts with explicit/fluent waits
- Enable via: `mvn clean test -P parallel`

---

## Configuration

### `config.properties` (default)
```properties
base.url=https://rahulshettyacademy.com/seleniumPractise/
browser=chrome
env=staging
headless=false
implicit.wait=0
explicit.wait=15
fluent.wait=20
```

### Environment-specific overrides
- `staging.properties` — staging base URL
- `prod.properties` — production base URL

System properties override file values:
```bash
mvn test -Dbrowser=edge -Dheadless=true -Denv=prod
```

---

## GitHub Repository

[https://github.com/Haaris96/greenkart-automation](https://github.com/Haaris96/greenkart-automation)
