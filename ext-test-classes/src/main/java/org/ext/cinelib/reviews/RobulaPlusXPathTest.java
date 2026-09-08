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
    public void testRobulaPlusXPath() throws Exception {
        driver.get(baseUrl + "movie/3");

        WebElement avg = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//*[contains(text(),'★ 4.7')]")
        ));
        assertTrue(avg.isDisplayed());

        WebElement author = driver.findElement(By.xpath("//*[contains(text(),'Dario')]"));
        assertEquals("Dario", author.getText());

        WebElement submit = driver.findElement(By.xpath("//*[contains(text(),'Post review')]"));
        assertEquals("Post review", submit.getText());
    }
}
