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
    public String getLocator() { return "SELENIUM_LOCATOR"; }

    @Test
    public void testSeleniumXPath() throws Exception {
        driver.get(baseUrl);
        // Playlists link in sidebar
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector(".nav-link-container:nth-child(4) > .flex")
            )
        ).click();

        // First playlist
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".ng-star-inserted:nth-child(1) > .card as-media-cover")
            )
        ).click();

        // Double click to start the first song
        WebElement song = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".ng-star-inserted:nth-child(1) > .playlist-tracks-grid > .ng-star-inserted:nth-child(2)")
            )
        );
        new Actions(driver).doubleClick(song).perform();

        // Go back home to refresh the now playing bar
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".nav-link-container:nth-child(1) > .flex")
            )
        ).click();
        Thread.sleep(1000);

        // Assert that the text of the current song is the correct one
        String text = driver.findElement(
            By.cssSelector(".ellipsis-one-line > .text-white")
        ).getText();
        assertEquals("Thriller", text);
    }

}
