package org.ext.cinelib.reviews;

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
        driver.get(baseUrl + "movie/3");
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'★ 4.7')]"))).isDisplayed());
        assertEquals("Dario", driver.findElement(By.xpath("//*[contains(text(),'Dario')]")).getText());
        assertEquals("Post review", driver.findElement(By.xpath("//*[contains(text(),'Post review')]")).getText());
    }
}
