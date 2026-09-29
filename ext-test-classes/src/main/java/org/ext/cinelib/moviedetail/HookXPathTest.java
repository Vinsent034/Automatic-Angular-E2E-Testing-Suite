package org.ext.cinelib.moviedetail;

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
        driver.get(baseUrl + "movie/3");
        assertEquals("Quiet Harbor", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@x-test-detail-title]"))).getText());
        assertEquals(1, driver.findElements(By.xpath("(//*[@x-test-cast-row])[2]")).size());
        assertEquals("Sam Whitfield", driver.findElement(By.xpath("(//*[@x-test-cast-name])[2]")).getText());
    }
}
