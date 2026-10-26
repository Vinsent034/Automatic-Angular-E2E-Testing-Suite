package org.unina.tests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class KatalonXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "KATALON_LOCATOR"; }

    @Test
    public void testKatalonXPath() throws Exception {
        driver.get(baseUrl);
        // Liked Songs navbar link
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='My Albums'])[1]/following::a[1]")
            )
        ).click();

        // Start the first song
        WebElement song = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Date added'])[1]/following::as-track-main-info[1]")
            )
        );
        new Actions(driver).doubleClick(song).perform();

        // Go back home to refresh the now playing bar
        driver.findElement(
            By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Search'])[1]/preceding::a[1]")
        ).click();

        // Now playing text
        String nowPlayingSong = driver.findElement(
            By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Megadeth'])[14]/following::button[1]")
        ).getText();

        // Play/Pause button
        WebElement playPauseButton = driver.findElement(
            By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Stop'])[1]/preceding::button[2]")
        );
        playPauseButton.click();
        Thread.sleep(200);
        playPauseButton.click();

        // Next song button
        driver.findElement(
            By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Stop'])[1]/preceding::div[13]")
        ).click();

        // Next playing song text for assertion
        String nextPlayingSong = driver.findElement(
            By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Megadeth'])[14]/following::button[1]")
        ).getText();

        assertNotEquals(nowPlayingSong, nextPlayingSong);
    }

}
