package com.dhl.transitcalculator.tests;

import com.dhl.transitcalculator.constants.Country;
import com.dhl.transitcalculator.constants.TestConstants;
import com.dhl.transitcalculator.page.TransitTimeCalculatorPage;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Transit Time Calculator – validation & basic happy paths.
 * Structure:
 *  - setup
 *  - shared helpers
 *  - nested groups: HappyPath, FieldValidation, Mismatch
 */
@DisplayName("Transit Calculator: Validation & Results")
class TransitCalculatorValidationTest extends BaseTest {

    private TransitTimeCalculatorPage page;

    // --------- Setup ---------
    @BeforeEach
    void openCalculator() {
        page = new TransitTimeCalculatorPage(driver, baseUrl).open();
        assertTrue(page.isAt(), "Calculator root should be visible");
    }

    // --------- Helpers ---------

    /** Clicks Calculate and waits until network/loader settles. */
    private void calculateAndWait() {
        page.clickCalculate().waitForNetworkToSettle();
    }

    private String originErr() {
        return page.originPostcodeErrorText().trim();
    }

    private String destinationErr() {
        return page.destinationPostcodeErrorText().trim();
    }

    /** Asserts both postcode fields show the exact same expected error text. */
    private void assertBothFieldErrors() {
        String origin = originErr();
        String destination = destinationErr();

        assertAll(
                () -> assertFalse(origin.isBlank(), "Origin error should not be empty"),
                () -> assertFalse(destination.isBlank(), "Destination error should not be empty"),
                () -> assertEquals(TestConstants.POSTAL_CODE_ERROR, origin, "Origin error text should match"),
                () -> assertEquals(TestConstants.POSTAL_CODE_ERROR, destination, "Destination error text should match")
        );
    }

    /** Asserts result overlay is shown (success path). */
    private void assertResultVisible() {
        assertAll(
                () -> assertTrue(page.isResultVisible(), "Result panel should be visible"),
                () -> assertTrue(page.isCalculatorOverlayVisible(), "Overlay should cover the calculator")
        );
    }

    // --------- Test Groups ---------

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("Valid CZ → SE shows results")
        void validCZtoSE_showsResult() {
            page
                    .selectOriginCountry(Country.CZECH_REPUBLIC)
                    .typeOriginPostcode(Country.CZECH_REPUBLIC.getValidPostcode())
                    .typeDestinationPostcode(Country.SWEDEN.getValidPostcode());

            calculateAndWait();
            assertResultVisible();
        }

        @Test
        @DisplayName("Valid SE → CZ shows results")
        void validSEtoCZ_showsResult() {
            page
                    .typeOriginPostcode(Country.SWEDEN.getValidPostcode())
                    .selectDestinationCountry(Country.CZECH_REPUBLIC)
                    .typeDestinationPostcode(Country.CZECH_REPUBLIC.getValidPostcode());

            calculateAndWait();
            assertResultVisible();
        }
    }

    @Nested
    @DisplayName("Field Validation")
    class FieldValidation {

        @Test
        @DisplayName("Empty submit shows both field errors (exact copy)")
        void emptySubmit_showsBothErrors() {
            calculateAndWait();
            assertBothFieldErrors();
        }

        @Test
        @DisplayName("Invalid numeric postcodes show both field errors (exact copy)")
        void invalidNumericPostcodes_showErrors() {
            page
                    .typeOriginPostcode(TestConstants.INVALID_POSTAL_CODE)
                    .typeDestinationPostcode(TestConstants.INVALID_POSTAL_CODE);

            calculateAndWait();
            assertBothFieldErrors();
        }

        @Test
        @DisplayName("Invalid string postcodes show both field errors (exact copy)")
        void invalidStringPostcodes_showErrors() {
            page
                    .typeOriginPostcode(TestConstants.INVALID_POSTAL_CODE_STRING)
                    .typeDestinationPostcode(TestConstants.INVALID_POSTAL_CODE_STRING);

            calculateAndWait();
            assertBothFieldErrors();
        }
    }

    @Nested
    @DisplayName("Country/Postcode Mismatch")
    class Mismatch {

        @Test
        @DisplayName("Mismatched postcodes vs countries shows global error and no result")
        void mismatchedCountries_showGlobalError_noResult() {
            page
                    // Origin: Sweden + CZ postcode (mismatch)
                    .selectOriginCountry(Country.SWEDEN)
                    .typeOriginPostcode(Country.CZECH_REPUBLIC.getValidPostcode())
                    // Destination: Czech Republic + SE postcode (mismatch)
                    .selectDestinationCountry(Country.CZECH_REPUBLIC)
                    .typeDestinationPostcode(Country.SWEDEN.getValidPostcode());

            calculateAndWait();

            String err = page.globalErrorText();

            assertAll(
                    () -> assertTrue(page.isGlobalErrorVisible(), "Global retrieval error should be visible"),
                    () -> assertFalse(err.isBlank(), "Global error text should not be empty"),
                    () -> assertFalse(page.isResultVisible(), "Result panel should NOT be visible"),
                    () -> assertTrue(err.contains(TestConstants.TOOL_UNAVAILABLE_ERROR),
                            "Global error should contain expected phrase (actual: " + err + ")")
            );
        }
    }
}
