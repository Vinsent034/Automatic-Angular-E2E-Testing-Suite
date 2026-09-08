package org.ext.cinelib.catalogsearch;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AbsoluteXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "ABSOLUTE_LOCATOR"; }

    @Test
    public void testAbsoluteXPath() throws Exception {
        driver.get(baseUrl);

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-catalog[1]/section[1]/div[1]/div[1]/input[1]")
        ));
        searchInput.clear();
        searchInput.sendKeys("quiet");
        Thread.sleep(500);

        List<WebElement> cards = driver.findElements(
            By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-catalog[1]/section[1]/div[3]/app-movie-card[1]/div[1]")
        );
        assertEquals(1, cards.size());

        WebElement cardTitle = driver.findElement(
            By.xpath("/html[1]/body[1]/app-root[1]/main[1]/app-catalog[1]/section[1]/div[3]/app-movie-card[1]/div[1]/div[1]/a[1]/h2[1]")
        );
        assertEquals("Quiet Harbor", cardTitle.getText());
    }
}
