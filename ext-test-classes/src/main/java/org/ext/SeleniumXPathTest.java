package org.ext;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class SeleniumXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "SELENIUM_LOCATOR"; }

    @Test
    public void testSeleniumXPath() throws Exception {
        driver.get(baseUrl);

        // Search link in sidebar
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//a[contains(text(),'Search')]")
        )).click();

        // Search input
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".input-container > .ng-untouched")
        ));
        searchInput.clear();
        searchInput.sendKeys("TonyPitony");
        Thread.sleep(1000);

        // Results
        List<WebElement> results = driver.findElement(
            By.cssSelector(".ng-star-inserted:nth-child(3) > .common-grid")
        ).findElements(By.xpath(".//*[normalize-space()='TonyPitony']"));
        assertFalse(results.isEmpty());
    }

}
