package org.ext.cinelib.catalogsearch;

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
        driver.get(baseUrl);
        WebElement s = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".search-input")));
        s.clear(); s.sendKeys("quiet"); Thread.sleep(500);
        assertEquals(1, driver.findElements(By.cssSelector(".movie-card")).size());
        assertEquals("Quiet Harbor", driver.findElement(By.cssSelector(".card-title")).getText());
    }
}
