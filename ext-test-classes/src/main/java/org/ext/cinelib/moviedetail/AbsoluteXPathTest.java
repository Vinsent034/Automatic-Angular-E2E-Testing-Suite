package org.ext.cinelib.moviedetail;

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
        driver.get(baseUrl + "movie/3");

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/header[1]/div[2]/h1[1]")
        ));
        assertEquals("Quiet Harbor", title.getText());

        assertEquals(1, driver.findElements(
            By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/section[2]/div[1]/app-cast-row[2]/div[1]")
        ).size());

        WebElement castName = driver.findElement(
            By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/section[2]/div[1]/app-cast-row[2]/div[1]/span[2]")
        );
        assertEquals("Sam Whitfield", castName.getText());
    }
}
