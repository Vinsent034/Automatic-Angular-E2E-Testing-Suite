package org.unina.tests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class RobulaXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "ROBULA_LOCATOR"; }

    @Test
    public void testRobulaXPath() throws Exception {
        driver.get(baseUrl);
        // Search link in sidebar
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@ng-reflect-router-link='/search']")
            )
        ).click();

        // Search input
        WebElement searchInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input")
            )
        );
        searchInput.clear();
        searchInput.sendKeys("Billie Jean");
        Thread.sleep(1000);

        // Double click to start the song
        WebElement song = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//as-album-track[@ng-reflect-index='1']/*/as-track-main-info")
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

        // Assert that the text of the current artist is the correct one
        String text = driver.findElement(
            By.xpath("//a[@class='text-white hover:underline']")
        ).getText();
        assertEquals("Billie Jean", text);
    }
}
