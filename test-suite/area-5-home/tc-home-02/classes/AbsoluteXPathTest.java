package org.ext;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.*;

public class AbsoluteXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "ABSOLUTE_LOCATOR"; }

    @Test
    public void testAbsoluteXPath() throws Exception {
        driver.get(baseUrl);

        // Greeting heading
        WebElement greeting = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-home/div/as-greeting/h2")
        ));
        assertFalse("Greeting is empty.", greeting.getText().trim().isEmpty());

        // First card title
        WebElement title = driver.findElement(
            By.xpath("/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-home/div/as-recent-played/div/as-card[1]/a/div[2]/h2")
        );
        assertFalse("Card title is empty.", title.getText().trim().isEmpty());
    }
}
