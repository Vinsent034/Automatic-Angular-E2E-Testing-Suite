package org.ext.cookbook.s3;

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
        driver.get(baseUrl + "recipe/1");
        assertEquals("Tomato Pasta", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@x-test-detail-title]"))).getText());
        WebElement servings = driver.findElement(By.xpath("//*[@x-test-servings]"));
        servings.sendKeys(Keys.chord(Keys.CONTROL, "a"), "4", Keys.TAB); Thread.sleep(700);
        assertEquals("400", driver.findElement(By.xpath("//*[@x-test-ing-qty='1']")).getText());
    }
}
