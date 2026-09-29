package org.ext.cookbook.s3;

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
        driver.get(baseUrl + "recipe/1");
        assertEquals("Tomato Pasta", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-recipe-detail[1]/article[1]/header[1]/div[1]/h1[1]"))).getText());
        WebElement servings = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-recipe-detail[1]/article[1]/div[1]/app-panel[1]/section[1]/div[1]/div[1]/label[1]/input[1]"));
        servings.sendKeys(Keys.chord(Keys.CONTROL, "a"), "4", Keys.TAB); Thread.sleep(700);
        assertEquals("400", driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-recipe-detail[1]/article[1]/div[1]/app-panel[1]/section[1]/div[1]/ul[1]/li[1]/app-ingredient-row[1]/div[1]/p[1]/span[1]")).getText());
    }
}
