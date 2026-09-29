package org.ext.cookbook.s6;

import org.ext.cookbook.CookBookBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.assertEquals;

public class RelativeXPathTest extends CookBookBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "stats");
        assertEquals("8", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[normalize-space()='8']"))).getText());
        assertEquals("3", driver.findElement(By.xpath("//span[@role='cell'][normalize-space()='3']")).getText());
        assertEquals("v1.0", driver.findElement(By.xpath("//span[@class='footer-version']")).getText());
    }
}
