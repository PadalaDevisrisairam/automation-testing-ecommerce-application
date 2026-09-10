# ECommerce Selenium Automation Tests

End-to-end UI tests for the **ShopPOC** ecommerce app using:

| Layer | Framework |
|---|---|
| Browser automation | Selenium WebDriver 4.18 |
| Driver management | WebDriverManager 5.7 |
| Test runner #1 | **TestNG 7.9** |
| Test runner #2 | **JUnit 5.10** |
| BDD | **Cucumber 7.16** (with TestNG runner) |
| Assertions | AssertJ 3.25 |
| Reporting | ExtentReports 5 + Allure 2.25 |

---

## Project Structure

```
selenium-tests/
├── pom.xml
└── src/test/
    ├── java/com/ecommerce/automation/
    │   ├── base/
    │   │   ├── BaseTest.java                  # TestNG base (ThreadLocal WebDriver)
    │   │   ├── BaseJUnitTest.java             # JUnit 5 base
    │   │   └── FailureScreenshotExtension.java# JUnit 5 TestWatcher
    │   ├── pages/                             # Page Object Model
    │   │   ├── BasePage.java
    │   │   ├── LoginPage.java
    │   │   ├── ProductsPage.java
    │   │   ├── CartPage.java
    │   │   └── NavbarPage.java
    │   ├── testng/                            # TestNG test classes
    │   │   ├── LoginTest.java
    │   │   ├── ProductsTest.java
    │   │   └── CartTest.java
    │   ├── junit/                             # JUnit 5 test classes
    │   │   ├── LoginJUnitTest.java
    │   │   ├── ProductsJUnitTest.java
    │   │   └── CartJUnitTest.java
    │   ├── cucumber/
    │   │   ├── runner/
    │   │   │   └── CucumberTestNGRunner.java  # BDD runner (TestNG)
    │   │   └── steps/
    │   │       ├── ScenarioContext.java       # PicoContainer shared state
    │   │       ├── Hooks.java                 # @Before / @After
    │   │       ├── LoginSteps.java
    │   │       ├── ProductsSteps.java
    │   │       └── CartSteps.java
    │   ├── reports/
    │   │   └── ExtentReportListener.java
    │   └── utils/
    │       ├── ConfigReader.java
    │       ├── DriverFactory.java
    │       ├── ScreenshotUtil.java
    │       └── TestData.java
    └── resources/
        ├── config.properties
        ├── allure.properties
        ├── features/
        │   ├── login.feature
        │   ├── products.feature
        │   └── cart.feature
        └── testng-suites/
            ├── testng-all.xml
            ├── testng-smoke.xml
            ├── testng-regression.xml
            └── testng-cucumber.xml
```

---

## Prerequisites

| Tool | Version |
|---|---|
| Java JDK | 11+ |
| Maven | 3.8+ |
| Chrome / Firefox / Edge | Latest |
| ShopPOC backend | Running on `http://localhost:8000` |
| ShopPOC frontend | Running on `http://localhost:5173` |

Start the app before running tests:

```bash
# Terminal 1 — backend
cd backend
uvicorn app.main:app --reload

# Terminal 2 — frontend
cd frontend
npm run dev
```

---

## Running Tests

### All TestNG tests (default)
```bash
mvn test
```

### TestNG — smoke only
```bash
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-suites/testng-smoke.xml
```

### TestNG — regression only
```bash
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-suites/testng-regression.xml
```

### JUnit 5 tests only
```bash
mvn test -P junit
```

### Cucumber BDD scenarios only
```bash
mvn test -P cucumber
```

### Cucumber — specific tag
```bash
mvn test -P cucumber -Dcucumber.filter.tags="@smoke"
mvn test -P cucumber -Dcucumber.filter.tags="@login and @regression"
```

### Run headless (no browser window)
```bash
mvn test -Dheadless=true
```

### Switch browser
```bash
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge
```

---

## Reports

| Report | Location | How to open |
|---|---|---|
| ExtentReports HTML | `target/reports/ExtentReport_<timestamp>.html` | Open in browser |
| Cucumber HTML | `target/cucumber-reports/cucumber.html` | Open in browser |
| Cucumber JSON | `target/cucumber-reports/cucumber.json` | CI integration |
| Allure results | `target/allure-results/` | See below |
| Screenshots | `target/screenshots/` | Auto-captured on failure |

### Generating the Allure HTML report
```bash
mvn allure:report
# Then open: target/site/allure-maven-plugin/index.html

# Or serve it live:
mvn allure:serve
```

---

## Configuration

Edit `src/test/resources/config.properties` or override any value via `-D` system property:

| Property | Default | Description |
|---|---|---|
| `base.url` | `http://localhost:5173` | Frontend URL |
| `api.url` | `http://localhost:8000` | Backend URL |
| `browser` | `chrome` | `chrome` / `firefox` / `edge` |
| `headless` | `false` | Run without a visible window |
| `implicit.wait` | `10` | Seconds for implicit waits |
| `page.load.timeout` | `30` | Seconds for page load |
| `screenshot.on.failure` | `true` | Auto-screenshot on failure |
| `valid.email` | `test@example.com` | Demo login email |
| `valid.password` | `password123` | Demo login password |

---

## Test Coverage Summary

| Page | TestNG | JUnit 5 | Cucumber (BDD) |
|---|---|---|---|
| Login | 8 tests | 8 tests | 8 scenarios |
| Products | 7 tests | 7 tests | 7 scenarios |
| Cart | 7 tests | 7 tests | 7 scenarios |

**Total: ~66 automated checks across 3 frameworks.**
