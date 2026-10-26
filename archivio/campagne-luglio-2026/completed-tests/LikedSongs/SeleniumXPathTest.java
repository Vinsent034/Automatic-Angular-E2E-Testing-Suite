package org.unina.tests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SeleniumXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "SELENIUM_LOCATOR"; }

    @Test
    public void testSeleniumXPath() throws Exception {
        driver.get(baseUrl);
        // Liked Songs navbar link
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector(".nav-link-container:nth-child(6) > .flex")
            )
        ).click();

        // Start the first song
        WebElement song = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".ng-star-inserted:nth-child(1) > .playlist-tracks-grid > .ng-star-inserted:nth-child(2)")
            )
        );
        new Actions(driver).doubleClick(song).perform();

        // Go back home to refresh the now playing bar
        driver.findElement(
            By.cssSelector(".nav-link-container:nth-child(1) > .flex")
        ).click();

        // Now playing text
        String nowPlayingSong = driver.findElement(
            By.cssSelector(".ellipsis-one-line > .text-white")
        ).getText();

        // Play/Pause button
        WebElement playPauseButton = driver.findElement(
            By.cssSelector(".text-black svg")
        );
        playPauseButton.click();
        Thread.sleep(200);
        playPauseButton.click();

        // Next song button
        driver.findElement(
            By.cssSelector(".svg-icon-step-forward > svg")
        ).click();

        // Next playing song text for assertion
        String nextPlayingSong = driver.findElement(
            By.cssSelector(".ellipsis-one-line > .text-white")
        ).getText();

        assertNotEquals(nowPlayingSong, nextPlayingSong);
    }

}
