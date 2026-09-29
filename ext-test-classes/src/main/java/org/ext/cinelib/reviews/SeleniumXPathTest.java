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
    public void test() throws Exception {
        driver.get(baseUrl + "movie/3");
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".reviews-average"))).isDisplayed());
        assertEquals("Dario", driver.findElement(By.cssSelector(".review-item:nth-child(1) .review-author")).getText());
        assertEquals("Post review", driver.findElement(By.cssSelector(".btn-primary")).getText());
    }
}
