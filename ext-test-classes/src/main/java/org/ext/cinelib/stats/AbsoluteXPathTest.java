package org.ext.cinelib.stats;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class AbsoluteXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "ABSOLUTE_LOCATOR"; }

    @Test
    public void testAbsoluteXPath() throws Exception {
        driver.get(baseUrl + "stats");

        WebElement total = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-stats[1]/section[1]/div[1]/div[1]/span[1]")
        ));
        assertEquals("8", total.getText());

        WebElement topTitle = driver.findElement(
            By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-stats[1]/section[1]/section[2]/table[1]/tbody[1]/tr[1]/td[2]/a[1]")
        );
        assertEquals("Quiet Harbor", topTitle.getText());

        WebElement genre = driver.findElement(
            By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-stats[1]/section[1]/section[1]/div[1]/div[1]/span[2]")
        );
        assertEquals("2", genre.getText());
    }
}
