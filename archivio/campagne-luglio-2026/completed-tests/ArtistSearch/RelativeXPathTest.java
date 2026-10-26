package org.unina.tests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class RelativeXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "RELATIVE_LOCATOR"; }

    @Test
    public void testRelativeXPath() throws Exception {
        driver.get(baseUrl);
        // Search link in sidebar
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//a[normalize-space()='Search']"))
        ).click();

        // Search input
        WebElement searchInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Artists, songs, albums, or playlists']")
            )
        );
        searchInput.clear();
        searchInput.sendKeys("Michael Jackson");
        Thread.sleep(1000);

        // Click the artist
        WebElement artistCardLink = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//as-card[@ng-reflect-title='Michael Jackson']//a[@class='card']")
        ));
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", artistCardLink);

        // Assert that the text of the current artist is the correct one
        String text = driver.findElement(
                By.xpath("//h2[normalize-space()='Michael Jackson']")
        ).getText();
        assertEquals("Michael Jackson", text);
    }
}
