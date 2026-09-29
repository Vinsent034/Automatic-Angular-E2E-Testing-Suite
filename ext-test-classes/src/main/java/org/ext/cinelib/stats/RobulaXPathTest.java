package org.ext.cinelib.stats;

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
    public void test() throws Exception {
        driver.get(baseUrl + "stats");
        assertEquals("8", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[1]/span[@class='stat-value']"))).getText());
        assertEquals("Quiet Harbor", driver.findElement(By.xpath("//a[@ng-reflect-router-link='/movie,3']")).getText());
        assertEquals("2", driver.findElement(By.xpath("//div[@data-genre='Adventure']/span[@class='genre-bar-count']")).getText());
    }
}
