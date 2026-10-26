package org.unina.tests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class SeleniumXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "SELENIUM_XPATH"; }

    @Test
    public void testSeleniumXPath() throws Exception {
        driver.get(baseUrl);
        // Search link in sidebar
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector(".nav-link-container:nth-child(2) > .flex")
            )
        ).click();

        // Search input
        WebElement searchInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".ng-dirty")
            )
        );
        searchInput.clear();
        searchInput.sendKeys("Billie Jean");
        Thread.sleep(1000);

        // Double click to start the song
        WebElement song = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".ng-star-inserted:nth-child(1) > .album-tracks-grid > .ng-star-inserted:nth-child(2)")
            )
        );
        new Actions(driver).doubleClick(song).perform();

        // Go back home to refresh the now playing bar
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.linkText("Home")
            )
        ).click();
        Thread.sleep(1000);

        // Assert that the text of the current artist is the correct one
        String text = driver.findElement(
            By.cssSelector(".ellipsis-one-line > .text-white")
        ).getText();
        assertEquals("Billie Jean", text);
    }

}
