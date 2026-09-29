package org.ext.cinelib.stats;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RelativeXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "RELATIVE_LOCATOR"; }

    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "stats");
        assertEquals("8", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[normalize-space()='8']"))).getText());
        assertEquals("Quiet Harbor", driver.findElement(By.xpath("//a[normalize-space()='Quiet Harbor']")).getText());
        assertEquals("2", driver.findElement(By.xpath("//div[@data-genre='Adventure']//span[@class='genre-bar-count'][normalize-space()='2']")).getText());
    }
}
