package org.ext.cinelib.stats;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class RobulaXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "ROBULA_LOCATOR"; }

    @Test
    public void testRobulaXPath() throws Exception {
        driver.get(baseUrl + "stats");

        WebElement total = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[1]/span[@class='stat-value']")
        ));
        assertEquals("8", total.getText());

        WebElement topTitle = driver.findElement(By.xpath("//a[@ng-reflect-router-link='/movie,3']"));
        assertEquals("Quiet Harbor", topTitle.getText());

        WebElement genre = driver.findElement(By.xpath("//div[@data-genre='Adventure']/span[@class='genre-bar-count']"));
        assertEquals("2", genre.getText());
    }
}
