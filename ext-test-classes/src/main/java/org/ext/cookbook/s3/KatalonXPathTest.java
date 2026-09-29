package org.ext.cookbook.s3;

import org.ext.cookbook.CookBookBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.assertEquals;

public class KatalonXPathTest extends CookBookBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "recipe/1");
        assertEquals("Tomato Pasta", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='numbers'])[1]/following::h1[1]"))).getText());
        WebElement servings = driver.findElement(By.id("servings-input"));
        servings.sendKeys(Keys.chord(Keys.CONTROL, "a"), "4", Keys.TAB); Thread.sleep(700);
        assertEquals("400", driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='recipe default: 2'])[1]/following::span[2]")).getText());
    }
}
