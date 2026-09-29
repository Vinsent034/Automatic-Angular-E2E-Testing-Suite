package org.ext.cookbook.s1;

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
        driver.get(baseUrl);
        WebElement s = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-recipe-list[1]/section[1]/div[2]/div[1]/label[1]/input[1]")));
        s.clear(); s.sendKeys("risotto"); Thread.sleep(700);
        driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-recipe-list[1]/section[1]/div[4]/ul[1]/li[1]/app-recipe-card[1]/article[1]/div[1]/div[1]"));
        assertEquals("Mushroom Risotto", driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-recipe-list[1]/section[1]/div[4]/ul[1]/li[1]/app-recipe-card[1]/article[1]/div[1]/div[1]/header[1]/div[1]/a[1]/h2[1]")).getText());
    }
}
