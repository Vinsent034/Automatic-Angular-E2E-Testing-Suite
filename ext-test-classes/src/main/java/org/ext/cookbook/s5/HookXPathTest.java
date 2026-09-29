package org.ext.cookbook.s5;

import org.ext.cookbook.CookBookBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.assertEquals;

public class HookXPathTest extends CookBookBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "shopping");
        assertEquals("2", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@x-test-open-count]"))).getText());
        driver.findElement(By.xpath("//*[@x-test-shop-check='1']")).click(); Thread.sleep(500);
        assertEquals("1", driver.findElement(By.xpath("//*[@x-test-open-count]")).getText());
        driver.findElement(By.xpath("//*[@x-test-clear-done]"));
    }
}
