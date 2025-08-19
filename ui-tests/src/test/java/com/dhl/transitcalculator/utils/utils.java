package com.dhl.transitcalculator.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class utils {
    public static void safeClick(WebDriver driver, WebElement element) {
        try {
            // Scroll into view
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
            Thread.sleep(1000);
            // Click
            element.click();
        } catch (Exception e) {
            throw new RuntimeException("Failed to safeClick element: " + element, e);
        }
    }
}
