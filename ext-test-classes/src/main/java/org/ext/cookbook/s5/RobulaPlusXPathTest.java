package org.ext.cookbook.s5;

import org.ext.cookbook.CookBookBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.assertEquals;

public class RobulaPlusXPathTest extends CookBookBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "shopping");
        assertEquals("2", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@data-kind='open']"))).getText());
        driver.findElement(By.xpath("//*[@aria-label='Bought Arborio rice']")).click(); Thread.sleep(500);
        assertEquals("1", driver.findElement(By.xpath("//*[@data-kind='open']")).getText());
        driver.findElement(By.xpath("//*[contains(text(),'Clear bought items')]"));
    }
}
