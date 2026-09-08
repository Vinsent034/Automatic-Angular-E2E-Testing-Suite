package org.ext.cinelib.reviews;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RobulaXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "ROBULA_LOCATOR"; }

    @Test
    public void testRobulaXPath() throws Exception {
        driver.get(baseUrl + "movie/3");

        WebElement avg = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//span[@class='reviews-average']")
        ));
        assertTrue(avg.isDisplayed());

        WebElement author = driver.findElement(By.xpath("//li[1]/*/span[@class='review-author']"));
        assertEquals("Dario", author.getText());

        WebElement submit = driver.findElement(By.xpath("//button[@type='submit']"));
        assertEquals("Post review", submit.getText());
    }
}
