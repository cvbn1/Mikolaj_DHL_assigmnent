package com.dhl.transitcalculator.constants;

/**
 * Enum representing supported countries with their ISO codes
 * and a valid sample postal code for testing.
 */
public enum Country {
    CZECH_REPUBLIC("CZ", "14800"),
    SWEDEN("SE", "26234");

    private final String code;
    private final String validPostcode;

    Country(String code, String validPostcode) {
        this.code = code;
        this.validPostcode = validPostcode;
    }

    /**
     * Returns the country code (used in dropdown select values).
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns a valid postal code for this country.
     */
    public String getValidPostcode() {
        return validPostcode;
    }
}
