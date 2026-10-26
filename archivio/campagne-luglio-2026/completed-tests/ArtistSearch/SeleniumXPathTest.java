package org.unina.tests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class SeleniumXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "SELENIUM_LOCATOR"; }

    @Test
    public void testSeleniumXPath() throws Exception {
        driver.get(baseUrl);
        // Search link in sidebar
        wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector(".nav-link-container:nth-child(2) > .flex"))
        ).click();

        // Search input
        WebElement searchInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".ng-dirty")
            )
        );
        searchInput.clear();
        searchInput.sendKeys("Michael Jackson");
        Thread.sleep(1000);

        // Click the artist
        WebElement artistCardLink = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".ng-star-inserted:nth-child(3) > .common-grid > .ng-star-inserted:nth-child(1) as-media-cover")
        ));
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", artistCardLink);

        // Assert that the text of the current artist is the correct one
        String text = driver.findElement(
            By.cssSelector(".media-title")
        ).getText();
        assertEquals("Michael Jackson", text);
    }

}
