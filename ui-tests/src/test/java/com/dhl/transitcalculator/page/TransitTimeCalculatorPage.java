package com.dhl.transitcalculator.page;

import com.dhl.transitcalculator.constants.Country;
import com.dhl.transitcalculator.utils.utils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for Transit Time Calculator.
 * Provides fluent API for interacting with the calculator and reading its state.
 */
public class TransitTimeCalculatorPage {

    // ====== TIMEOUTS / CONFIG ======
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration LOADER_APPEAR_TIMEOUT = Duration.ofSeconds(2);
    private static final Duration LOADER_DISAPPEAR_TIMEOUT = Duration.ofSeconds(10);

    // ====== DRIVER / CONTEXT ======
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
    private final By calculateButton       = By.cssSelector(".c-calculator button");
    private final By calculateButtonLoader = By.cssSelector(".c-calculator button.is-loading");

    // ====== RESULT / OVERLAY ======
    private final By resultPanel       = By.cssSelector(".js--leadtime--options-container");
    private final By calculatorOverlay = By.cssSelector(".c-calculator--countryselector-overlay");

    // ====== GLOBAL ERROR ======
    private final By globalError = By.cssSelector(".c-calculator--error-message.js--freight-coutries-general-error-message");

    // ====== CONSTRUCTOR ======
    public TransitTimeCalculatorPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
        this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
    }

    // ====== NAVIGATION ======

    /** Opens the calculator page and waits until root section is visible. */
    public TransitTimeCalculatorPage open() {
        driver.get(baseUrl);
        waitVisible(sectionRoot);
        return this;
    }

    /** Quick check that we are on the correct page. */
    public boolean isAt() {
        return isVisible(sectionRoot);
    }

    // ====== ORIGIN ACTIONS ======

    /** Selects origin country by code or visible text. */
    public TransitTimeCalculatorPage selectOriginCountry(String country) {
        openDropdownAndPick(originCountryDropdown, country);
        return this;
    }

    /** Fills in the origin postcode (clear + sendKeys). */
    public TransitTimeCalculatorPage typeOriginPostcode(String postcode) {
        clearAndType(originPostcodeInput, postcode);
        return this;
    }

    /** Current value of the origin postcode field. */
    public String originPostcodeValue() {
        return getValue(originPostcodeInput);
    }

    // ====== DESTINATION ACTIONS ======

    /** Selects destination country by code or visible text. */
    public TransitTimeCalculatorPage selectDestinationCountry(String country) {
        openDropdownAndPick(destinationCountryDropdown, country);
        return this;
    }

    /**
     * Fills in the destination postcode (clear + sendKeys).
     */
    public void typeDestinationPostcode(String postcode) {
        clearAndType(destinationPostcodeInput, postcode);
    }

    /** Current value of the destination postcode field. */
    public String destinationPostcodeValue() {
        return getValue(destinationPostcodeInput);
    }

    // ====== SUBMIT / LOADING ======

    /** Clicks the Calculate button (with safe click) and returns the page object. */
    public TransitTimeCalculatorPage clickCalculate() {
        WebElement button = waitClickable(calculateButton);
        utils.safeClick(driver, button);
        return this;
    }

    /**
     * Waits for loader to appear and disappear in a reasonable sequence.
     */
    public void waitForNetworkToSettle() {
        waitAppear(calculateButtonLoader);
        waitDisappear(calculateButtonLoader);
    }

    // ====== VALIDATION MESSAGES ======

    /** Error text under the origin postcode field (empty if not visible). */
    public String originPostcodeErrorText() {
        return getTextSafe(originPostcodeError);
    }

    /** Error text under the destination postcode field (empty if not visible). */
    public String destinationPostcodeErrorText() {
        return getTextSafe(destinationPostcodeError);
    }

    /** Returns true if a global error is visible. */
    public boolean isGlobalErrorVisible() {
        return isVisible(globalError);
    }

    /** Returns global error text (empty if not visible). */
    public String globalErrorText() {
        return getTextSafe(globalError);
    }

    // ====== RESULT STATE ======

    /** Returns true if the result panel is visible. */
    public boolean isResultVisible() {
        return isVisible(resultPanel);
    }

    /** Returns true if the calculator overlay is visible. */
    public boolean isCalculatorOverlayVisible() {
        return isVisible(calculatorOverlay);
    }

    // ====== PRIVATE HELPERS ======

    private WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private boolean isVisible(By locator) {
        try {
            return waitVisible(locator).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    private void clearAndType(By locator, String text) {
        WebElement el = waitVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    private String getValue(By locator) {
        try {
            return waitVisible(locator).getAttribute("value");
        } catch (TimeoutException e) {
            return "";
        }
    }

    private String getTextSafe(By locator) {
        try {
            return waitVisible(locator).getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }

    private void waitAppear(By locator) {
        try {
            new WebDriverWait(driver, TransitTimeCalculatorPage.LOADER_APPEAR_TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException ignored) { /* no-op */ }
    }

    private void waitDisappear(By locator) {
        try {
            new WebDriverWait(driver, TransitTimeCalculatorPage.LOADER_DISAPPEAR_TIMEOUT).until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException ignored) { /* no-op */ }
    }

    /**
     * Selects a value in dropdown:
     * - if it is a native &lt;select&gt;, use Selenium Select (by value or visible text),
     * - otherwise click custom dropdown and choose option.
     */
    private void openDropdownAndPick(By dropdownRoot, String valueOrText) {
        WebElement root = waitClickable(dropdownRoot);

        if ("select".equalsIgnoreCase(root.getTagName())) {
            Select select = new Select(root);
            try {
                select.selectByValue(valueOrText);
            } catch (NoSuchElementException e) {
                select.selectByVisibleText(valueOrText);
            }
            return;
        }

        // Custom dropdown fallback
        utils.safeClick(driver, root);
        String id = root.getAttribute("id");
        By option = By.cssSelector("#" + id + " > option[value='" + valueOrText + "'], #" + id + " option:contains('" + valueOrText + "')");
        WebElement optionEl = waitClickable(option);
        utils.safeClick(driver, optionEl);
    }

    // ====== OVERLOADS (enum-friendly) ======

    public TransitTimeCalculatorPage selectOriginCountry(Country country) {
        return selectOriginCountry(country.getCode());
    }

    public TransitTimeCalculatorPage selectDestinationCountry(Country country) {
        return selectDestinationCountry(country.getCode());
    }
}
