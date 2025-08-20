package com.dhl.transitcalculator.tests;

import com.dhl.transitcalculator.page.TransitTimeCalculatorPage;
import com.dhl.transitcalculator.constants.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransitCalculatorValidationTest extends BaseTest {

    private TransitTimeCalculatorPage page;

    @BeforeEach
    void openCalculator() {
        page = new TransitTimeCalculatorPage(driver, baseUrl)
                .open();
    }

    @Test
    @DisplayName("Happy path: valid CZ → SE input shows result panel")
    void submittingValidForm_showsResultPanel() {
        page
                .selectOriginCountry("CZ")
                .typeOriginPostcode(TestConstants.VALID_CZ_POSTAL_CODE)
                .typeDestinationPostcode(TestConstants.VALID_SE_POSTAL_CODE)
                .clickCalculate()
                .waitForNetworkToSettle();

        assertTrue(
                page.isResultVisible(),
                "Result panel should be visible for valid CZ→SE input"
        );
    }

    @Test
    @DisplayName("Empty submit shows validation on both fields (visible + exact copy)")
    void submittingEmptyForm_showsBothErrorMessages() {
        page.clickCalculate()
            .waitForNetworkToSettle();

        String originErr = page.originPostcodeErrorText().trim();
        String destinationErr = page.destinationPostcodeErrorText().trim();

        assertAll(
                // visible & non-empty
                () -> assertFalse(originErr.isBlank(), "Origin error should not be empty"),
                () -> assertFalse(destinationErr.isBlank(), "Destination error should not be empty"),

                // exact copy (business expectation)
                () -> assertEquals(TestConstants.POSTAL_CODE_ERROR, originErr,
                        "Origin error text should match definition"),
                () -> assertEquals(TestConstants.POSTAL_CODE_ERROR, destinationErr,
                        "Destination error text should match definition")
        );
    }

    @Test
    @DisplayName("Invalid postal codes: shows validation on both fields (visible + exact copy)")
    void submittingInvalidPostalCodes_showsExpectedErrorTexts() {
        page
                .typeOriginPostcode(TestConstants.INVALID_POSTAL_CODE)
                .typeDestinationPostcode(TestConstants.INVALID_POSTAL_CODE)
                .clickCalculate()
                .waitForNetworkToSettle();

        String originErr = page.originPostcodeErrorText().trim();
        String destinationErr = page.destinationPostcodeErrorText().trim();

        assertAll(
                // visible & non-empty
                () -> assertFalse(originErr.isBlank(), "Origin error should not be empty"),
                () -> assertFalse(destinationErr.isBlank(), "Destination error should not be empty"),

                // exact business copy
                () -> assertEquals(TestConstants.POSTAL_CODE_ERROR, originErr,
                        "Origin error text should match definition"),
                () -> assertEquals(TestConstants.POSTAL_CODE_ERROR, destinationErr,
                        "Destination error text should match definition")
        );
    }

    @Test
    @DisplayName("Mismatched postcodes vs countries → shows global retrieval error and no result")
    void submittingMismatchedPostcodes_showsGlobalError_noResult() {
        page
                // Origin: Sweden + CZ postcode (mismatch)
                .selectOriginCountry("SE")
                .typeOriginPostcode(TestConstants.VALID_CZ_POSTAL_CODE)
                // Destination: Czech Republic + SE postcode (mismatch)
                .selectDestinationCountry("CZ")
                .typeDestinationPostcode(TestConstants.VALID_SE_POSTAL_CODE)
                .clickCalculate()
                .waitForNetworkToSettle();

        String err = page.globalErrorText();

        assertAll(
                () -> assertTrue(page.isGlobalErrorVisible(), "Global retrieval error should be visible"),
                () -> assertFalse(err.isBlank(), "Global retrieval error text should not be empty"),
                () -> assertFalse(page.isResultVisible(), "Result panel should NOT be visible"),
                () -> assertTrue(
                         err.contains(TestConstants.TOOL_UNAVAILABLE_ERROR),
                        "Global error should mention 'unable to retrieve data' (actual: " + err + ")"
                )
        );
    }

}