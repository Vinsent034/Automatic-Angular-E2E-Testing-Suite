package org.ext.flowboard.s1;

import org.ext.flowboard.FlowBoardBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.assertEquals;

public class KatalonXPathTest extends FlowBoardBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl);
        WebElement s = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search-input")));
        s.clear(); s.sendKeys("login"); Thread.sleep(700);
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='In Progress'])[1]/following::article[1]"));
        assertEquals("Design login", driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='high'])[1]/following::h4[1]")).getText());
    }
}
