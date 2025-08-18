package com.dhl.transitcalculator.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Minimal WebDriver lifecycle for tests.
 * - Fresh ChromeDriver per test.
 * - Headless via -Dheadless=true/false (default: true).
 * - Cookie banner handled if present.
 */
public abstract class BaseTest {

    protected WebDriver driver;
    protected String baseUrl;

    @BeforeEach
    void setUp() {
        String headlessProp = System.getProperty("headless", "true");
        baseUrl = System.getProperty("baseUrl",
                "https://www.dhl.com/se-en/home/freight/tools/european-road-freight-transit-time-calculator.html");

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(headlessProp)) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36");

        driver = new ChromeDriver(options);

        // Navigate to baseUrl
        driver.get(baseUrl);

        // Handle cookie banner (non-blocking)
        acceptCookiesIfVisible();
    }

    private void acceptCookiesIfVisible() {
        // 1) quick existence check (0s) — avoids waiting when banner is absent
        // OneTrust usually uses id="onetrust-accept-btn-handler"
        By acceptBtn = By.id("onetrust-accept-btn-handler");

        if (!driver.findElements(acceptBtn).isEmpty()) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
                WebElement button = wait.until(ExpectedConditions.elementToBeClickable(acceptBtn));
                button.click();
            } catch (TimeoutException ignored) {
                // present but not clickable → ignore for now
            }
        }
        // (Optional) fallback selectors if site A/B tests different markup:
        // By.cssSelector("button#onetrust-accept-btn-handler, button[aria-label='Agree to all']");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
