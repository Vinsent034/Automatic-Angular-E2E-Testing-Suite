package org.ext.cinelib.catalogsearch;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HookXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "HOOK_LOCATOR"; }

    @Test
    public void test() throws Exception {
        driver.get(baseUrl);
        WebElement s = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@x-test-search-input]")));
        s.clear(); s.sendKeys("quiet"); Thread.sleep(500);
        assertEquals(1, driver.findElements(By.xpath("//*[@x-test-card]")).size());
        assertEquals("Quiet Harbor", driver.findElement(By.xpath("//*[@x-test-card-title]")).getText());
    }
}
