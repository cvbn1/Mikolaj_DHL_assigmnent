# DHL Transit Time Calculator – Test Automation

This project contains **UI test automation** for the [DHL European Road Freight Transit Time Calculator](https://www.dhl.com/se-en/home/freight/tools/european-road-freight-transit-time-calculator.html).

The goal is to demonstrate clean **test design**, **Page Object Model**, and stable **Selenium WebDriver automation** using JUnit 5.

---

## Tech Stack

* **Java 17+**
* **JUnit 5** (unit test framework)
* **Selenium WebDriver**
* **WebDriverManager** (automatic driver binaries)
* **Maven/Gradle** (build + dependencies)

---

## Project Structure

```
src
 └── test
      └── java
           └── com.dhl.transitcalculator
                ├── page/
                │    └── TransitTimeCalculatorPage.java     # Page Object (encapsulates locators & actions)
                ├── tests/
                │    ├── BaseTest.java                      # WebDriver lifecycle + setup
                │    └── TransitCalculatorValidationTest.java # Test scenarios
                └── constants/
                     ├── Country.java                       # Enum with supported countries & valid postcodes
                     └── TestConstants.java                 # Common validation/error strings
```

---

## Getting Started

### Prerequisites

* Java 17+
* Maven or Gradle
* Chrome browser installed

### Install dependencies

```bash
mvn clean install
```

### Run the tests

```bash
mvn test
```

By default, the tests will:

* Launch Chrome in headless mode
* Open the DHL Transit Calculator page
* Run validation and result scenarios

---


## Implemented Test Scenarios

* **Happy paths**

  * Valid CZ → SE input shows results
  * Valid SE → CZ input shows results
* **Validation**

  * Empty form shows field errors
  * Invalid numeric postcodes show errors
  * Invalid string postcodes show errors
* **Negative**

  * Country/postcode mismatch shows global error, no result

---

## Highlights

* **Page Object Model** for clean, maintainable locators & actions
* **Reusable helpers** (safe click, wait for loaders, error readers)
* **Stable tests** (all clicks via `utils.safeClick`, overlay handling)
* **Readable assertions** (`assertAll`, descriptive messages)

---

## Next Steps (Future Improvements)

* Add user journey where user is able to edit the calculator by pressing the edit previous step button
* Add parameterized tests for more country combinations
* Generate HTML or Allure reports
* Add cross-browser support

---

## Author

Created by Miroslav Mikolaj
