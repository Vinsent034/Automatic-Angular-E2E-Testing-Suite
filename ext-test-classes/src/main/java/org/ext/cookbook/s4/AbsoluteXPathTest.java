package org.ext.cookbook.s4;

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
        driver.get(baseUrl + "recipe/new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-recipe-form[1]/section[1]/form[1]/div[1]/div[1]/div[1]/input[1]")));
        driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-recipe-form[1]/section[1]/form[1]/div[1]/div[2]/div[1]/select[1]"));
        assertEquals("Create recipe", driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-recipe-form[1]/section[1]/form[1]/div[2]/div[1]/button[1]")).getText());
    }
}
