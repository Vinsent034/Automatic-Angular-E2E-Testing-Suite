package org.ext.cinelib.stats;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class KatalonXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "KATALON_LOCATOR"; }

    @Test
    public void testKatalonXPath() throws Exception {
        driver.get(baseUrl + "stats");

        WebElement total = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Statistics'])[2]/following::span[1]")
        ));
        assertEquals("8", total.getText());

        WebElement topTitle = driver.findElement(By.linkText("Quiet Harbor"));
        assertEquals("Quiet Harbor", topTitle.getText());

        WebElement genre = driver.findElement(
            By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Adventure'])[1]/following::span[1]")
        );
        assertEquals("2", genre.getText());
    }
}
