package org.unina.tests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class RobulaXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "ROBULA_LOCATOR"; }

    @Test
    public void testRobulaXPath() throws InterruptedException {
        driver.get(baseUrl);
        // Playlists link in sidebar
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@ng-reflect-router-link='/collection/playlists']")
            )
        ).click();

        // First playlist
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[@ng-reflect-router-link='/playlist/0vhooTWkjMKTZXvnXthX'][@_ngcontent-ng-c810943523='']")
            )
        ).click();

        // Double click to start the first song
        WebElement song = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//as-playlist-track[@ng-reflect-index='0']/*")
            )
        );
        new Actions(driver).doubleClick(song).perform();

        // Go back home to refresh the now playing bar
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[@ng-reflect-router-link='']")
            )
        ).click();
        Thread.sleep(1000);

        // Assert that the text of the current song is the correct one
        String text = driver.findElement(
            By.xpath("//a[@class='text-white hover:underline']")
        ).getText();
        assertEquals("Thriller", text);
    }
}
