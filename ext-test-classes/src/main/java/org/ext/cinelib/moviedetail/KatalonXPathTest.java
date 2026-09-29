package org.ext.cinelib.moviedetail;

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
        driver.get(baseUrl + "movie/3");
        assertEquals("Quiet Harbor", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Q'])[1]/following::h1[1]"))).getText());
        assertEquals(1, driver.findElements(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Edie'])[1]/following::div[1]")).size());
        assertEquals("Sam Whitfield", driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Edie'])[1]/following::span[2]")).getText());
    }
}
