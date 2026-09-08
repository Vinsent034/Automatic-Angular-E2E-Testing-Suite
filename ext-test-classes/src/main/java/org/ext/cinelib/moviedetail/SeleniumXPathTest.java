package org.ext.cinelib.moviedetail;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class SeleniumXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "SELENIUM_LOCATOR"; }

    @Test
    public void testSeleniumXPath() throws Exception {
        driver.get(baseUrl + "movie/3");

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".detail-title")
        ));
        assertEquals("Quiet Harbor", title.getText());

        assertEquals(1, driver.findElements(
            By.cssSelector("app-cast-row:nth-child(2) > .cast-row")
        ).size());

        WebElement castName = driver.findElement(
            By.cssSelector("app-cast-row:nth-child(2) .cast-name")
        );
        assertEquals("Sam Whitfield", castName.getText());
    }
}
