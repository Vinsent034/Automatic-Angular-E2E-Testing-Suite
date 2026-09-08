package org.ext.cinelib.catalogsearch;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class RobulaXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "ROBULA_LOCATOR"; }

    @Test
    public void testRobulaXPath() throws Exception {
        driver.get(baseUrl);

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//input")
        ));
        searchInput.clear();
        searchInput.sendKeys("quiet");
        Thread.sleep(500);

        List<WebElement> cards = driver.findElements(
            By.xpath("//div[@class='movie-card movie-card-grid is-favorite']")
        );
        assertEquals(1, cards.size());

        WebElement cardTitle = driver.findElement(By.xpath("//h2"));
        assertEquals("Quiet Harbor", cardTitle.getText());
    }
}
