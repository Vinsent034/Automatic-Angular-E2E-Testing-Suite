package org.unina.tests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class RobulaPlusXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "ROBULAPLUS_LOCATOR"; }

    @Test
    public void testRobulaXPath() throws Exception {
        driver.get(baseUrl);
        // Playlists link in sidebar
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@ng-reflect-router-link='/collection/playlists']")
            )
        ).click();

        // First playlist
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@_ngcontent-ng-c810943523='' and @ng-reflect-router-link='/playlist/0vhooTWkjMKTZXvnXthX']")
            )
        ).click();

        // Double click to start the first song
        WebElement song = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@ng-reflect-index='0']/*")
            )
        );
        new Actions(driver).doubleClick(song).perform();

        // Go back home to refresh the now playing bar
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@ng-reflect-router-link='']")
            )
        ).click();
        Thread.sleep(1000);

        // Assert that the text of the current song is the correct one
        String text = driver.findElement(
            By.xpath("//*[@class='text-white hover:underline']")
        ).getText();
        assertEquals("Thriller", text);
    }

}
