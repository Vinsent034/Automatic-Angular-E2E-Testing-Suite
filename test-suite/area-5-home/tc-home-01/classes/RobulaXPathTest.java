package org.ext;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.Assert.*;

public class RobulaXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "ROBULA_LOCATOR"; }

    @Test
    public void testRobulaXPath() throws Exception {
        driver.get(baseUrl);

        // Home link in sidebar
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//a[@ng-reflect-router-link='']")
        )).click();

        // Recently Played heading
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//h2[normalize-space()='Recently Played']")
        ));

        // Cards in the recently-played grid
        List<WebElement> cards = driver.findElement(
            By.xpath("//div[@class='common-grid ng-star-inserted']")
        ).findElements(By.cssSelector("as-card"));
        assertFalse("No recently played cards found.", cards.isEmpty());
    }
}
