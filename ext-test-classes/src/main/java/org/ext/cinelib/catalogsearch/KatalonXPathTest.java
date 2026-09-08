package org.ext.cinelib.catalogsearch;

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class KatalonXPathTest extends CineLibBaseTest {
    @Override
    public String getLocator() { return "KATALON_LOCATOR"; }

    @Test
    public void testKatalonXPath() throws Exception {
        driver.get(baseUrl);

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//input[@type='text']")
        ));
        searchInput.clear();
        searchInput.sendKeys("quiet");
        Thread.sleep(500);

        List<WebElement> cards = driver.findElements(
            By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Reset catalog'])[1]/following::div[2]")
        );
        assertEquals(1, cards.size());

        WebElement cardTitle = driver.findElement(
            By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='♥'])[1]/following::h2[1]")
        );
        assertEquals("Quiet Harbor", cardTitle.getText());
    }
}
