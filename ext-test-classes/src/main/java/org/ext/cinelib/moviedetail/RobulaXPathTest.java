package org.ext.cinelib.moviedetail;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class RobulaXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "ROBULA_LOCATOR"; }

    @Test
    public void testRobulaXPath() throws Exception {
        driver.get(baseUrl + "movie/3");

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//h1")
        ));
        assertEquals("Quiet Harbor", title.getText());

        assertEquals(1, driver.findElements(
            By.xpath("//app-cast-row[2]/*")
        ).size());

        WebElement castName = driver.findElement(
            By.xpath("//app-cast-row[2]/*/span[@class='cast-name']")
        );
        assertEquals("Sam Whitfield", castName.getText());
    }
}
