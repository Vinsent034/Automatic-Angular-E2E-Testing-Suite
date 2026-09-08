package org.ext.cinelib.stats;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class RelativeXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "RELATIVE_LOCATOR"; }

    @Test
    public void testRelativeXPath() throws Exception {
        driver.get(baseUrl + "stats");

        WebElement total = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//span[normalize-space()='8']")
        ));
        assertEquals("8", total.getText());

        WebElement topTitle = driver.findElement(By.xpath("//a[normalize-space()='Quiet Harbor']"));
        assertEquals("Quiet Harbor", topTitle.getText());

        WebElement genre = driver.findElement(
            By.xpath("//div[@data-genre='Adventure']//span[@class='genre-bar-count'][normalize-space()='2']")
        );
        assertEquals("2", genre.getText());
    }
}
