package org.ext.cinelib.reviews;

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
        driver.get(baseUrl + "movie/3");

        WebElement avg = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/section[3]/app-reviews[1]/div[1]/div[1]/span[1]")
        ));
        assertTrue(avg.isDisplayed());

        WebElement author = driver.findElement(
            By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/section[3]/app-reviews[1]/div[1]/ul[1]/li[1]/div[1]/span[1]")
        );
        assertEquals("Dario", author.getText());

        WebElement submit = driver.findElement(
            By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/section[3]/app-reviews[1]/div[1]/form[1]/div[3]/button[1]")
        );
        assertEquals("Post review", submit.getText());
    }
}
