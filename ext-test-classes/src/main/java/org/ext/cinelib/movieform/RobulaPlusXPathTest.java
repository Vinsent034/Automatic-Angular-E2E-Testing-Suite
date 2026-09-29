package org.ext.cinelib.movieform;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RobulaPlusXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "ROBULA_PLUS_LOCATOR"; }

    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "movie/new");
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='title']"))).isDisplayed());
        assertTrue(driver.findElement(By.xpath("//select")).isDisplayed());
        assertEquals("Create movie", driver.findElement(By.xpath("//*[contains(text(),'Create movie')]")).getText());
    }
}
