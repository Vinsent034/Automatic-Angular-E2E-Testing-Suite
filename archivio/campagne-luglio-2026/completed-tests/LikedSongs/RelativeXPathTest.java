package org.unina.tests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RelativeXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "RELATIVE_LOCATOR"; }

    @Test
    public void testRelativeXPath() throws Exception {
        driver.get(baseUrl);
        // Liked Songs navbar link
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='Liked songs']")
            )
        ).click();

        // Start the first song
        WebElement song = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//as-playlist-track[1]//as-media-table-row[1]//as-track-main-info[1]")
            )
        );
        new Actions(driver).doubleClick(song).perform();

        // Go back home to refresh the now playing bar
        driver.findElement(
            By.xpath("//a[normalize-space()='Home']")
        ).click();

        // Now playing text
        String nowPlayingSong = driver.findElement(
            By.xpath("//a[@class='text-white hover:underline']")
        ).getText();

        // Play/Pause button
        WebElement playPauseButton = driver.findElement(
            By.xpath("//button[@class='flex play-button control-button text-black bg-white']")
        );
        playPauseButton.click();
        Thread.sleep(200);
        playPauseButton.click();

        // Next song button
        driver.findElement(
            By.xpath("//div[@class='now-playing-bar-center']//div[2]")
        ).click();

        // Next playing song text for assertion
        String nextPlayingSong = driver.findElement(
            By.xpath("//a[@class='text-white hover:underline']")
        ).getText();

        assertNotEquals(nowPlayingSong, nextPlayingSong);
    }
}
