package org.ext.cookbook.s4;

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
        driver.get(baseUrl + "recipe/new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='f-title']")));
        driver.findElement(By.xpath("//select[@id='f-course']"));
        assertEquals("Create recipe", driver.findElement(By.xpath("//button[@type='submit']")).getText());
    }
}
