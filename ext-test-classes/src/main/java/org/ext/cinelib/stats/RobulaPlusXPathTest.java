package org.ext.cinelib.stats;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class RobulaPlusXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "ROBULA_PLUS_LOCATOR"; }

    @Test
    public void testRobulaPlusXPath() throws Exception {
        driver.get(baseUrl + "stats");

        WebElement total = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//span[contains(text(),'8')]")
        ));
        assertEquals("8", total.getText());

        WebElement topTitle = driver.findElement(By.xpath("//*[contains(text(),'Quiet Harbor')]"));
        assertEquals("Quiet Harbor", topTitle.getText());

        WebElement genre = driver.findElement(By.xpath("//*[@data-genre='Adventure']/*[contains(text(),'2')]"));
        assertEquals("2", genre.getText());
    }
}
