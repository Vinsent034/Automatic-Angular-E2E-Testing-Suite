package org.ext.cookbook.s2;

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
        WebElement veg = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-recipe-list[1]/section[1]/div[2]/div[3]/label[1]/input[1]")));
        if (!veg.isSelected()) veg.click();
        new Select(driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-recipe-list[1]/section[1]/div[2]/div[2]/label[1]/select[1]"))).selectByValue("dessert"); Thread.sleep(700);
        assertEquals("1", driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-recipe-list[1]/section[1]/div[3]/p[1]/span[1]")).getText());
    }
}
