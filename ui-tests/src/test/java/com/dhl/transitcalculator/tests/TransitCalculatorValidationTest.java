package com.dhl.transitcalculator.tests;

import com.dhl.transitcalculator.page.TransitTimeCalculatorPage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;  // assertAll, assertFalse, assertEquals, ...

public class TransitCalculatorValidationTest extends BaseTest {

    @Test
    void submittingEmptyForm_showsBothErrorMessages_nonEmpty() {
        TransitTimeCalculatorPage page = new TransitTimeCalculatorPage(driver, baseUrl)
                .open()
                .clickCalculate();

        String originErr = page.originPostcodeErrorText();
        String destErr   = page.destinationPostcodeErrorText();

        assertAll(
                () -> assertFalse(originErr == null || originErr.trim().isEmpty(),
                        "Origin postcode error should be visible and non-empty"),
                () -> assertFalse(destErr == null || destErr.trim().isEmpty(),
                        "Destination postcode error should be visible and non-empty")
        );
    }

    @Test
    void submittingEmptyForm_showsExpectedErrorTexts() {
        TransitTimeCalculatorPage page = new TransitTimeCalculatorPage(driver, baseUrl)
                .open()
                .clickCalculate();

        assertEquals("Correct postal code (e.g. no post box)*", page.originPostcodeErrorText());
        assertEquals("Correct postal code (e.g. no post box)*", page.destinationPostcodeErrorText());
    }
}