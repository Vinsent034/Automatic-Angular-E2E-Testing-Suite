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

public class RobulaPlusXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "ROBULAPLUS_LOCATOR"; }

    @Test
    public void testRobulaXPath() throws Exception {
        driver.get(baseUrl);

        // Search link in sidebar
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//*[@ng-reflect-router-link='/search']")
        )).click();

        // Search input
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//input")
        ));
        searchInput.clear();
        searchInput.sendKeys("TonyPitony");
        Thread.sleep(1000);

        // Results
        List<WebElement> results = driver.findElement(
            By.xpath("//*[3]/*[@class='common-grid']")
        ).findElements(By.xpath(".//*[normalize-space()='TonyPitony']"));
        assertFalse(results.isEmpty());
    }

}
