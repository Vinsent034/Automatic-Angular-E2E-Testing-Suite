package org.ext.cinelib.movieform;

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
    public void testSeleniumXPath() throws Exception {
        driver.get(baseUrl + "movie/new");

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("title")
        ));
        assertTrue(title.isDisplayed());

        WebElement genre = driver.findElement(By.id("genre"));
        assertTrue(genre.isDisplayed());

        WebElement submit = driver.findElement(By.cssSelector(".btn-primary"));
        assertEquals("Create movie", submit.getText());
    }
}
