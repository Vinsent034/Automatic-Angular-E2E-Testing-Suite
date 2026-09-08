package org.ext.cinelib.movieform;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AbsoluteXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "ABSOLUTE_LOCATOR"; }

    @Test
    public void testAbsoluteXPath() throws Exception {
        driver.get(baseUrl + "movie/new");

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-movie-form[1]/section[1]/form[1]/div[1]/input[1]")
        ));
        assertTrue(title.isDisplayed());

        WebElement genre = driver.findElement(
            By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-movie-form[1]/section[1]/form[1]/div[2]/div[2]/select[1]")
        );
        assertTrue(genre.isDisplayed());

        WebElement submit = driver.findElement(
            By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-movie-form[1]/section[1]/form[1]/div[7]/button[1]")
        );
        assertEquals("Create movie", submit.getText());
    }
}
