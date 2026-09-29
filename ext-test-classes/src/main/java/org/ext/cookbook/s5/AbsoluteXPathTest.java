package org.ext.cookbook.s5;

import org.ext.cookbook.CookBookBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.assertEquals;

public class AbsoluteXPathTest extends CookBookBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "shopping");
        assertEquals("2", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-shopping-list[1]/section[1]/div[2]/p[1]/span[1]"))).getText());
        driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-shopping-list[1]/section[1]/div[3]/div[1]/ul[1]/li[1]/label[1]/input[1]")).click(); Thread.sleep(500);
        assertEquals("1", driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-shopping-list[1]/section[1]/div[2]/p[1]/span[1]")).getText());
        driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-shopping-list[1]/section[1]/div[4]/div[1]/button[1]"));
    }
}
