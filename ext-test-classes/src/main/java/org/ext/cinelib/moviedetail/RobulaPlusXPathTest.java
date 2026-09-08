package org.ext.cinelib.moviedetail;

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
        driver.get(baseUrl + "movie/3");

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//h1")
        ));
        assertEquals("Quiet Harbor", title.getText());

        assertEquals(1, driver.findElements(
            By.xpath("//*[2]/*[@class='cast-row']")
        ).size());

        WebElement castName = driver.findElement(
            By.xpath("//*[contains(text(),'Sam Whitfield')]")
        );
        assertEquals("Sam Whitfield", castName.getText());
    }
}
