package org.unina.tests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AbsoluteXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "ABSOLUTE_LOCATOR"; }

    @Test
    public void testAbsoluteXPath() throws Exception {
        driver.get(baseUrl);
        // Liked Songs navbar link
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-nav-bar[1]/ul[1]/li[6]/a[1]")
            )
        ).click();

        // Start the first song
        WebElement song = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-main-view[1]/div[2]/as-tracks[1]/div[1]/div[1]/as-playlist-track[1]/as-media-table-row[1]/as-track-main-info[1]")
            )
        );
        new Actions(driver).doubleClick(song).perform();

        // Go back home to refresh the now playing bar
        driver.findElement(
            By.xpath("/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-nav-bar[1]/ul[1]/li[1]/a[1]")
        ).click();

        // Now playing text
        String nowPlayingSong = driver.findElement(
            By.xpath("/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-now-playing-bar[1]/footer[1]/div[1]/as-track-current-info[1]/div[2]/div[1]/a[1]")
        ).getText();

        // Play/Pause button
        WebElement playPauseButton = driver.findElement(
            By.xpath("/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-now-playing-bar[1]/footer[1]/div[2]/as-player-controls[1]/div[1]/as-play-button[1]/button[1]")
        );
        playPauseButton.click();
        Thread.sleep(200);
        playPauseButton.click();

        // Next song button
        driver.findElement(
            By.xpath("/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-now-playing-bar[1]/footer[1]/div[2]/as-player-controls[1]/div[1]/div[2]")
        ).click();

        // Next playing song text for assertion
        String nextPlayingSong = driver.findElement(
            By.xpath("/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-now-playing-bar[1]/footer[1]/div[1]/as-track-current-info[1]/div[2]/div[1]/a[1]")
        ).getText();

        assertNotEquals(nowPlayingSong, nextPlayingSong);
    }

}
