package org.ext.cinelib.stats;

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
        driver.get(baseUrl + "stats");
        assertEquals("8", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'8')]"))).getText());
        assertEquals("Quiet Harbor", driver.findElement(By.xpath("//*[contains(text(),'Quiet Harbor')]")).getText());
        assertEquals("2", driver.findElement(By.xpath("//*[@data-genre='Adventure']/*[contains(text(),'2')]")).getText());
    }
}
