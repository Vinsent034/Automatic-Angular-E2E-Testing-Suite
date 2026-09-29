package org.ext.flowboard.s1;

import org.ext.flowboard.FlowBoardBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.assertEquals;

public class RelativeXPathTest extends FlowBoardBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl);
        WebElement s = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='search-input']")));
        s.clear(); s.sendKeys("login"); Thread.sleep(700);
        driver.findElement(By.xpath("//article[@aria-label='Card Design login']"));
        assertEquals("Design login", driver.findElement(By.xpath("//h4[normalize-space()='Design login']")).getText());
    }
}
