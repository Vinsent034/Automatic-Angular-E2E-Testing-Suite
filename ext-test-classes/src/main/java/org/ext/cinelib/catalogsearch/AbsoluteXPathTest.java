package org.ext.cinelib.catalogsearch;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AbsoluteXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "ABSOLUTE_LOCATOR"; }

    @Test
    public void test() throws Exception {
        driver.get(baseUrl);
        WebElement s = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-catalog[1]/section[1]/div[1]/div[1]/input[1]")));
        s.clear(); s.sendKeys("quiet"); Thread.sleep(500);
        assertEquals(1, driver.findElements(By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-catalog[1]/section[1]/div[3]/app-movie-card[1]/div[1]")).size());
        assertEquals("Quiet Harbor", driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-catalog[1]/section[1]/div[3]/app-movie-card[1]/div[1]/div[1]/a[1]/h2[1]")).getText());
    }
}
