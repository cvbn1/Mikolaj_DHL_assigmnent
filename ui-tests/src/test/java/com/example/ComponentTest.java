package com.example;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComponentTest extends BaseUiTest {

    @Test
    void googleSearchInput_isVisible() {
        driver.get("https://www.google.com");
        WebElement searchBox = driver.findElement(By.name("q"));
        assertTrue(searchBox.isDisplayed(), "Search input má byť viditeľný");
        assertTrue(searchBox.isEnabled(), "Search input má byť aktívny");
    }
}
