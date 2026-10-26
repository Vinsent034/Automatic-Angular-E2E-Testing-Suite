package org.unina.tests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class KatalonXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "KATALON_XPATH"; }

    @Test
    public void testKatalonXPath() throws Exception {
        driver.get(baseUrl);
        // Search link in sidebar
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Home'])[1]/following::a[1]")
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
                By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Title'])[1]/following::as-track-main-info[1]")
            )
        );
        new Actions(driver).doubleClick(song).perform();

        // Go back home to refresh the now playing bar
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Search'])[1]/preceding::a[1]")
            )
        ).click();
        Thread.sleep(1000);

        // Assert that the text of the current artist is the correct one
        String text = driver.findElement(
            By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Michael Jackson'])[10]/following::a[1]")
        ).getText();
        assertEquals("Billie Jean", text);
    }

}
