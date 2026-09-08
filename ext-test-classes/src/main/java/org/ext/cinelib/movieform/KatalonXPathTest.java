package org.ext.cinelib.movieform;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KatalonXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "KATALON_LOCATOR"; }

    @Test
    public void testKatalonXPath() throws Exception {
        driver.get(baseUrl + "movie/new");

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("title")
        ));
        assertTrue(title.isDisplayed());

        WebElement genre = driver.findElement(By.id("genre"));
        assertTrue(genre.isDisplayed());

        WebElement submit = driver.findElement(By.xpath("//button[@type='submit']"));
        assertEquals("Create movie", submit.getText());
    }
}
