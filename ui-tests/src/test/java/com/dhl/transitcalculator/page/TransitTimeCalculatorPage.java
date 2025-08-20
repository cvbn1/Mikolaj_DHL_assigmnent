package com.dhl.transitcalculator.page;

import com.dhl.transitcalculator.utils.utils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/**
 * Transit Time Calculator – Page Object.
 */
public class TransitTimeCalculatorPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    // ====== ROOT / SECTION ======
    private final By sectionRoot = By.cssSelector(".c-calculator");

    // ====== ORIGIN ======
    private final By originCountryDropdown = By.cssSelector("#origin-country");
    private final By originPostcodeInput   = By.cssSelector("[data-di-id='#origin-postcode']");
    private final By originPostcodeError   = By.cssSelector("[class*='origin-zip-error']");

    // ====== DESTINATION ======
    private final By destinationCountryDropdown = By.cssSelector("#destination-country");
    private final By destinationPostcodeInput   = By.cssSelector("[data-di-id='#destination-postcode']");
    private final By destinationPostcodeError   = By.cssSelector("[class*='destination-zip-error']");

    // ====== CTA ======
    private final By calculateButton = By.cssSelector(".c-calculator button");

    // ====== RESULT / LOADER ======
    private final By resultPanel = By.cssSelector(".js--leadtime--options-container");
    private final By loader      = By.cssSelector(".c-calculator button.is-loading");

    // ====== GLOBAL ERROR (shipment retrieval) ======
    private final By globalError = By.cssSelector(".c-calculator--error-message.js--freight-coutries-general-error-message");


    private utils WebDriverUtils;

    // ====== CONSTRUCTOR ======
    public TransitTimeCalculatorPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ====== NAVIGATION ======
    public TransitTimeCalculatorPage open() {
        driver.get(baseUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(sectionRoot));
        return this;
    }

    // ====== ORIGIN ACTIONS ======
    public TransitTimeCalculatorPage selectOriginCountry(String countryVisibleText) {
        openDropdownAndPick(originCountryDropdown, countryVisibleText);
        return this;
    }

    public TransitTimeCalculatorPage typeOriginPostcode(String postcode) {
        clearAndType(originPostcodeInput, postcode);
        return this;
    }

    // ====== DESTINATION ACTIONS ======
    public TransitTimeCalculatorPage selectDestinationCountry(String countryVisibleText) {
        openDropdownAndPick(destinationCountryDropdown, countryVisibleText);
        return this;
    }

    public TransitTimeCalculatorPage typeDestinationPostcode(String postcode) {
        clearAndType(destinationPostcodeInput, postcode);
        return this;
    }

    // ====== SUBMIT ======
    public TransitTimeCalculatorPage clickCalculate() {
        WebElement button = waitUntilClickable(calculateButton);
        utils.safeClick(driver, button);
        return this;
    }

    public TransitTimeCalculatorPage waitForLoaderToAppear(long seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(loader));
        } catch (TimeoutException ignored) {  }
        return this;
    }

    public void waitForLoaderToDisappear(long seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(ExpectedConditions.invisibilityOfElementLocated(loader));
        } catch (TimeoutException ignored) {  }
    }

    public void waitForNetworkToSettle() {
        waitForLoaderToAppear(2)
                .waitForLoaderToDisappear(10);
    }

    // ====== VALIDATION MESSAGES ======
    public String originPostcodeErrorText() {
        return getTextSafe(originPostcodeError);
    }

    public String destinationPostcodeErrorText() {
        return getTextSafe(destinationPostcodeError);
    }

    public boolean isGlobalErrorVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(globalError)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String globalErrorText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(globalError)).getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }


    public boolean isResultVisible() {
        return isVisible(resultPanel);
    }

    // ====== PRIVATE HELPERS ======
    private WebElement waitUntilVisible(By sel) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(sel));
    }

    private WebElement waitUntilClickable(By sel) {
        return wait.until(ExpectedConditions.elementToBeClickable(sel));
    }

    private void clearAndType(By sel, String text) {
        WebElement el = waitUntilVisible(sel);
        el.clear();
        el.sendKeys(text);
    }

    private boolean isVisible(By sel) {
        try {
            return waitUntilVisible(sel).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    private String getTextSafe(By sel) {
        try {
            return waitUntilVisible(sel).getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }

    /**
     * Generic handler for dropdowns.
     * If the element is a native <select>, use Selenium Select API instead.
     * If the dropdown is custom, adjust the option locator accordingly.
     */
    private void openDropdownAndPick(By dropdownRoot, String countryCode) {
        WebElement root = waitUntilClickable(dropdownRoot);
        root.click();
        By option = By.cssSelector("#" + root.getAttribute("id") + " > option[value='" + countryCode + "']");
        waitUntilClickable(option).click();
    }
}
