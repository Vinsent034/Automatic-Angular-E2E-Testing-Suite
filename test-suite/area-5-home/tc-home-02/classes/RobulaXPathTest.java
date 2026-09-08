package org.ext;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.*;

public class RobulaXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "ROBULA_LOCATOR"; }

    @Test
    public void testRobulaXPath() throws Exception {
        driver.get(baseUrl);

        // Greeting heading
        WebElement greeting = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//as-greeting/h2")
        ));
        assertFalse("Greeting is empty.", greeting.getText().trim().isEmpty());

        // First card title
        WebElement title = driver.findElement(
            By.xpath("//as-card[1]//h2")
        );
        assertFalse("Card title is empty.", title.getText().trim().isEmpty());
    }
}
