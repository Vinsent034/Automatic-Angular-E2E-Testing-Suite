package org.unina.tests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RobulaXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "ROBULA_LOCATOR"; }

    @Test
    public void testRobulaXPath() throws InterruptedException {
        driver.get(baseUrl);
        // Liked Songs navbar link
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@ng-reflect-router-link='/collection/tracks']")
            )
        ).click();

        // Start the first song
        WebElement song = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//as-playlist-track[@ng-reflect-index='0']/*/as-track-main-info")
            )
        );
        new Actions(driver).doubleClick(song).perform();

        // Go back home to refresh the now playing bar
        driver.findElement(
            By.xpath("//a[@ng-reflect-router-link='']")
        ).click();

        // Now playing text
        String nowPlayingSong = driver.findElement(
            By.xpath("//a[@class='text-white hover:underline']")
        ).getText();

        // Play/Pause button
        WebElement playPauseButton = driver.findElement(
            By.xpath("//as-play-button[@_ngcontent-ng-c38434958='']/*")
        );
        playPauseButton.click();
        Thread.sleep(200);
        playPauseButton.click();

        // Next song button
        driver.findElement(
            By.xpath("//div[@x-test-hook-div-5=''][@_ngcontent-ng-c38434958='']")
        ).click();

        // Next playing song text for assertion
        String nextPlayingSong = driver.findElement(
            By.xpath("//a[@class='text-white hover:underline']")
        ).getText();

        assertNotEquals(nowPlayingSong, nextPlayingSong);
    }
}
