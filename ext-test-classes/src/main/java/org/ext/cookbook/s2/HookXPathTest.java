package org.ext.cookbook.s2;

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
        driver.get(baseUrl);
        WebElement veg = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@x-test-veg-only]")));
        if (!veg.isSelected()) veg.click();
        new Select(driver.findElement(By.xpath("//*[@x-test-course-filter]"))).selectByValue("dessert"); Thread.sleep(700);
        assertEquals("1", driver.findElement(By.xpath("//*[@x-test-result-count]")).getText());
    }
}
