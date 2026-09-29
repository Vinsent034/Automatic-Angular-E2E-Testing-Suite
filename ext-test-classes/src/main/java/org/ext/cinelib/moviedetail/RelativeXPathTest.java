package org.ext.cinelib.moviedetail;

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
        driver.get(baseUrl + "movie/3");
        assertEquals("Quiet Harbor", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Quiet Harbor']"))).getText());
        assertEquals(1, driver.findElements(By.xpath("//body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/section[2]/div[1]/app-cast-row[2]/div[1]")).size());
        assertEquals("Sam Whitfield", driver.findElement(By.xpath("//span[normalize-space()='Sam Whitfield']")).getText());
    }
}
