package org.ext.cinelib.stats;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KatalonXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "KATALON_LOCATOR"; }

    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "stats");
        assertEquals("8", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Statistics'])[2]/following::span[1]"))).getText());
        assertEquals("Quiet Harbor", driver.findElement(By.linkText("Quiet Harbor")).getText());
        assertEquals("2", driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Adventure'])[1]/following::span[1]")).getText());
    }
}
