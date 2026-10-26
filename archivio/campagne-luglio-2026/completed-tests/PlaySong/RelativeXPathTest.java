package org.unina.tests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class RelativeXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "RELATIVE_XPATH"; }

    @Test
    public void testRelativeXPath() throws Exception {
        driver.get(baseUrl);
        // Search link in sidebar
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='Search']")
            )
        ).click();

        // Search input
        WebElement searchInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Artists, songs, albums, or playlists'])")
            )
        );
        searchInput.clear();
        searchInput.sendKeys("Billie Jean");
        Thread.sleep(1000);

        // Double click to start the song
        WebElement song = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//as-album-track[1]//as-media-table-row[1]//as-track-main-info[1]")
            )
        );
        new Actions(driver).doubleClick(song).perform();

        // Go back home to refresh the now playing bar
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[normalize-space()='Home']")
            )
        ).click();
        Thread.sleep(1000);

        // Assert that the text of the current artist is the correct one
        String text = driver.findElement(
            By.xpath("//as-track-current-info/div[2]/div/a")
        ).getText();
        assertEquals("Billie Jean", text);
    }
}
