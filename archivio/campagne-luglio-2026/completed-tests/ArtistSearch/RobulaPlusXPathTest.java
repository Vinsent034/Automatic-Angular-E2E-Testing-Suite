package org.unina.tests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class RobulaPlusXPathTest extends BaseTest {
    @Override
    public String getLocator() { return "ROBULAPLUS_LOCATOR"; }

    @Test
    public void testRobulaXPath() throws Exception {
        driver.get(baseUrl);
        // Search link in sidebar
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@ng-reflect-router-link='/search']")
            )
        ).click();

        // Search input
        WebElement searchInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input")
            )
        );
        searchInput.clear();
        searchInput.sendKeys("Michael Jackson");
        Thread.sleep(1000);

        // Click the artist
        WebElement artistCardLink = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//*[@ng-reflect-title='Michael Jackson']")
        ));
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", artistCardLink);

        // Assert that the text of the current artist is the correct one
        String text = driver.findElement(
            By.xpath("//*[@x-test-hook-h2-4='']")
        ).getText();
        assertEquals("Michael Jackson", text);
    }

}
