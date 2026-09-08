package org.ext.cinelib.stats;

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
        driver.get(baseUrl + "stats");

        WebElement total = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".stat-card:nth-child(1) > .stat-value")
        ));
        assertEquals("8", total.getText());

        WebElement topTitle = driver.findElement(By.linkText("Quiet Harbor"));
        assertEquals("Quiet Harbor", topTitle.getText());

        WebElement genre = driver.findElement(By.cssSelector(".genre-bar-row:nth-child(1) > .genre-bar-count"));
        assertEquals("2", genre.getText());
    }
}
