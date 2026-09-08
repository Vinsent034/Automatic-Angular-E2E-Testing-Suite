package org.ext.cinelib.reviews;

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
        driver.get(baseUrl + "movie/3");

        WebElement avg = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".reviews-average")
        ));
        assertTrue(avg.isDisplayed());

        WebElement author = driver.findElement(By.cssSelector(".review-item:nth-child(1) .review-author"));
        assertEquals("Dario", author.getText());

        WebElement submit = driver.findElement(By.cssSelector(".btn-primary"));
        assertEquals("Post review", submit.getText());
    }
}
