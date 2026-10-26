package org.unina.tests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class AbsoluteXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "ABSOLUTE_LOCATOR"; }

    @Test
    public void testAbsoluteXPath() throws Exception {
        driver.get(baseUrl);
        // Search link in sidebar
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/angular-spotify-root/as-layout/as-nav-bar/ul/li[2]/a")
            )
        ).click();

        // Search input
        WebElement searchInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-search/div/div[1]/as-input/div/input")
            )
        );
        searchInput.clear();
        searchInput.sendKeys("Michael Jackson");
        Thread.sleep(1000);

        // Click the artist
        WebElement artistCardLink = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-search/div/div[3]/div/as-card[1]/a")
        ));
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", artistCardLink);

        // Assert that the text of the current artist is the correct one
        String text = driver.findElement(
                By.xpath("/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-artist/div/as-media-summary/div/h2")
        ).getText();
        assertEquals("Michael Jackson", text);
    }

}
