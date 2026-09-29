package org.ext.cinelib.movieform;

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
        driver.get(baseUrl + "movie/new");
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-movie-form[1]/section[1]/form[1]/div[1]/input[1]"))).isDisplayed());
        assertTrue(driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-movie-form[1]/section[1]/form[1]/app-panel[1]/section[1]/div[1]/div[1]/div[2]/select[1]")).isDisplayed());
        assertEquals("Create movie", driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-movie-form[1]/section[1]/form[1]/div[6]/button[1]")).getText());
    }
}
