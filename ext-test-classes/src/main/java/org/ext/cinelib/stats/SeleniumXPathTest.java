package org.ext.cinelib.stats;

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
        driver.get(baseUrl + "stats");
        assertEquals("8", wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".stat-card:nth-child(1) > .stat-value"))).getText());
        assertEquals("Quiet Harbor", driver.findElement(By.linkText("Quiet Harbor")).getText());
        assertEquals("2", driver.findElement(By.cssSelector(".genre-bar-row:nth-child(1) > .genre-bar-count")).getText());
    }
}
