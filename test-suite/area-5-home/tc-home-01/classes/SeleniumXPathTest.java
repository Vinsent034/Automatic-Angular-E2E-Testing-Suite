package org.ext;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.Assert.*;

public class SeleniumXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "SELENIUM_LOCATOR"; }

    @Test
    public void testSeleniumXPath() throws Exception {
        driver.get(baseUrl);

        // Home link in sidebar
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//a[contains(text(),'Home')]")
        )).click();

        // Recently Played heading
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("as-recent-played > .text-heading")
        ));

        // Cards in the recently-played grid
        List<WebElement> cards = driver.findElement(
            By.cssSelector(".common-grid")
        ).findElements(By.cssSelector("as-card"));
        assertFalse("No recently played cards found.", cards.isEmpty());
    }
}
