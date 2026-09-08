package org.ext;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.*;

public class RobulaPlusXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "ROBULAPLUS_LOCATOR"; }

    @Test
    public void testRobulaPlusXPath() throws Exception {
        driver.get(baseUrl);

        // Greeting heading
        WebElement greeting = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//as-greeting//*[self::h1 or self::h2]")
        ));
        assertFalse("Greeting is empty.", greeting.getText().trim().isEmpty());

        // First card title
        WebElement title = driver.findElement(
            By.xpath("//as-card//h2[contains(@class,'card-title')]")
        );
        assertFalse("Card title is empty.", title.getText().trim().isEmpty());
    }
}
