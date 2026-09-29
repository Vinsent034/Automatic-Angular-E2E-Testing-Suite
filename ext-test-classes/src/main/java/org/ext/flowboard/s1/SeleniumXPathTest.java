package org.ext.flowboard.s1;

import org.ext.flowboard.FlowBoardBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.assertEquals;

public class SeleniumXPathTest extends FlowBoardBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl);
        WebElement s = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search-input")));
        s.clear(); s.sendKeys("login"); Thread.sleep(700);
        driver.findElement(By.cssSelector(".card"));
        assertEquals("Design login", driver.findElement(By.cssSelector(".card-title")).getText());
    }
}
