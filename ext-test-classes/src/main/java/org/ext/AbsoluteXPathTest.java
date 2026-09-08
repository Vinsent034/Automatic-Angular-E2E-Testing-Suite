package org.ext;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class AbsoluteXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "ABSOLUTE_LOCATOR"; }

    @Test
    public void testAbsoluteXPath() throws Exception {
        driver.get(baseUrl);

        // Search link in sidebar
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("/html/body/angular-spotify-root/as-layout/as-nav-bar/ul/li[2]/a")
        )).click();

        // Search input
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-search/div/div[1]/as-input/div/input")
        ));
        searchInput.clear();
        searchInput.sendKeys("TonyPitony");
        Thread.sleep(1000);

        // Results
        List<WebElement> results = driver.findElement(
            By.xpath("/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-search/div/div[3]/div")
        ).findElements(By.xpath(".//h2[normalize-space()='TonyPitony']"));
        assertFalse(results.isEmpty());
    }
}
