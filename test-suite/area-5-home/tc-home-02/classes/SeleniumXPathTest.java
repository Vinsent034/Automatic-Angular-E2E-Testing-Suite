package org.ext;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.*;

public class SeleniumXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "SELENIUM_LOCATOR"; }

    @Test
    public void testSeleniumXPath() throws Exception {
        driver.get(baseUrl);

        // Greeting heading
        WebElement greeting = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("as-greeting h2")
        ));
        assertFalse("Greeting is empty.", greeting.getText().trim().isEmpty());

        // First card title
        WebElement title = driver.findElement(
            By.cssSelector("as-card .card-title")
        );
        assertFalse("Card title is empty.", title.getText().trim().isEmpty());
    }
}
