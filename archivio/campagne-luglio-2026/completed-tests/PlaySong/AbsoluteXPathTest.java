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
    public String getLocator() { return "ABSOLUTE_XPATH"; }

    @Test
    public void testAbsoluteXPath() throws Exception {
        driver.get(baseUrl);
        // Search link in sidebar
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-nav-bar[1]/ul[1]/li[2]/a[1]")
            )
        ).click();

        // Search input
        WebElement searchInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-main-view[1]/div[2]/as-search[1]/div[1]/div[1]/as-input[1]/div[1]/input[1]")
            )
        );
        searchInput.clear();
        searchInput.sendKeys("Billie Jean");
        Thread.sleep(1000);

        // Double click to start the song
        WebElement song = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-main-view[1]/div[2]/as-search[1]/div[1]/div[2]/div[1]/as-album-track[1]/as-media-table-row[1]/as-track-main-info[1]")
            )
        );
        new Actions(driver).doubleClick(song).perform();

        // Go back home to refresh the now playing bar
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-nav-bar[1]/ul[1]/li[1]/a[1]")
            )
        ).click();
        Thread.sleep(1000);

        // Assert that the text of the current artist is the correct one
        String text = driver.findElement(
            By.xpath("/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-now-playing-bar[1]/footer[1]/div[1]/as-track-current-info[1]/div[2]/div[1]/a[1]")
        ).getText();
        assertEquals("Billie Jean", text);
    }

}
