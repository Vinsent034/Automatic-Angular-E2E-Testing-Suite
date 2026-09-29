package org.ext.cookbook.s1;

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
        WebElement s = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@x-test-search]")));
        s.clear(); s.sendKeys("risotto"); Thread.sleep(700);
        driver.findElement(By.xpath("//*[@x-test-card='2']"));
        assertEquals("Mushroom Risotto", driver.findElement(By.xpath("//*[@x-test-card-title='2']")).getText());
    }
}
