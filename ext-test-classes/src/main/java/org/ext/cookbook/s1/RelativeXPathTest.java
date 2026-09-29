package org.ext.cookbook.s1;

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
        driver.get(baseUrl);
        WebElement s = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='search-input']")));
        s.clear(); s.sendKeys("risotto"); Thread.sleep(700);
        driver.findElement(By.xpath("//div[@class='card-inner']"));
        assertEquals("Mushroom Risotto", driver.findElement(By.xpath("//h2[normalize-space()='Mushroom Risotto']")).getText());
    }
}
