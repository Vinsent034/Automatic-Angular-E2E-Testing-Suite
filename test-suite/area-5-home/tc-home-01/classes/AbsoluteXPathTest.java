package org.ext;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.Assert.*;

public class AbsoluteXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "ABSOLUTE_LOCATOR"; }

    @Test
    public void testAbsoluteXPath() throws Exception {
        driver.get(baseUrl);

        // Home link in sidebar
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("/html/body/angular-spotify-root/as-layout/as-nav-bar/ul/li[1]/a")
        )).click();

        // Recently Played heading
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-home/div/as-recent-played/h2")
        ));

        // Cards in the recently-played grid
        List<WebElement> cards = driver.findElement(
            By.xpath("/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-home/div/as-recent-played/div")
        ).findElements(By.cssSelector("as-card"));
        assertFalse("No recently played cards found.", cards.isEmpty());
    }
}
