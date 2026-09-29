package org.ext.cookbook.s6;

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
        driver.get(baseUrl + "stats");
        assertEquals("8", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-stats[1]/section[1]/div[2]/div[1]/div[1]/span[1]"))).getText());
        assertEquals("3", driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-stats[1]/section[1]/div[3]/app-panel[1]/section[1]/div[1]/div[1]/div[2]/span[2]")).getText());
        assertEquals("v1.0", driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/footer[1]/div[1]/p[2]/span[2]")).getText());
    }
}
