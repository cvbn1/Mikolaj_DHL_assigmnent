package com.dhl.transitcalculator.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class utils {
    public static void safeClick(WebDriver driver, WebElement element) {
        try {
            // Scroll into view (center)
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});", element);

            // Wait for element to be clickable
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.elementToBeClickable(element));

            // Perform click
            element.click();
        } catch (Exception e) {
            throw new RuntimeException("Failed to safeClick element: " + element, e);
        }
    }

}
