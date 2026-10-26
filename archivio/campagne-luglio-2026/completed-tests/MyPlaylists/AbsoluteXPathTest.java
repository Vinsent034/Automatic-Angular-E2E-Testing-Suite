package org.unina.tests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class AbsoluteXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "ABSOLUTE_LOCATOR"; }

    @Test
    public void testAbsoluteXPath() throws Exception {
        driver.get(baseUrl);
        // Playlists link in sidebar
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/angular-spotify-root/as-layout/as-nav-bar/ul/li[4]/a")
            )
        ).click();

        // First playlist
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-playlists/div/as-playlist-list/div/as-card[1]/a")
            )
        ).click();

        // Double click to start the first song
        WebElement song = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-playlist/div[2]/div/as-playlist-track[1]/as-media-table-row")
            )
        );
        new Actions(driver).doubleClick(song).perform();

        // Go back home to refresh the now playing bar
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/angular-spotify-root/as-layout/as-nav-bar/ul/li[1]/a")
            )
        ).click();
        Thread.sleep(1000);

        // Assert that the text of the current song is the correct one
        String text = driver.findElement(
            By.xpath("/html/body/angular-spotify-root/as-layout/as-now-playing-bar/footer/div[1]/as-track-current-info/div[2]/div[1]/a")
        ).getText();
        assertEquals("Thriller", text);
    }

}
