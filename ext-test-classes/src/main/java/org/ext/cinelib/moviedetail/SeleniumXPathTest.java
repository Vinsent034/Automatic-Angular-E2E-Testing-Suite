package org.ext.cinelib.moviedetail;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SeleniumXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "SELENIUM_LOCATOR"; }

    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "movie/3");
        assertEquals("Quiet Harbor", wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".detail-title"))).getText());
        assertEquals(1, driver.findElements(By.cssSelector("app-cast-row:nth-child(2) > .cast-row")).size());
        assertEquals("Sam Whitfield", driver.findElement(By.cssSelector("app-cast-row:nth-child(2) .cast-name")).getText());
    }
}
