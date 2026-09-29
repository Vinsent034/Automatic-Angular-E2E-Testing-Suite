package org.ext.cinelib.catalogsearch;

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
        driver.get(baseUrl);
        WebElement s = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='text']")));
        s.clear(); s.sendKeys("quiet"); Thread.sleep(500);
        assertEquals(1, driver.findElements(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Reset catalog'])[1]/following::div[2]")).size());
        assertEquals("Quiet Harbor", driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='♥'])[1]/following::h2[1]")).getText());
    }
}
