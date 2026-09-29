package org.ext.cookbook.s3;

import org.ext.cookbook.CookBookBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.assertEquals;

public class RobulaXPathTest extends CookBookBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "recipe/1");
        assertEquals("Tomato Pasta", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1"))).getText());
        WebElement servings = driver.findElement(By.xpath("//input"));
        servings.sendKeys(Keys.chord(Keys.CONTROL, "a"), "4", Keys.TAB); Thread.sleep(700);
        assertEquals("400", driver.findElement(By.xpath("//div[@data-ingredient-id='1']/*/span[@class='ing-qty']")).getText());
    }
}
