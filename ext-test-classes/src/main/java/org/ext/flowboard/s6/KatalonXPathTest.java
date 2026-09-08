package org.ext.flowboard.s6;

import org.ext.flowboard.FlowBoardBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertEquals;

public class KatalonXPathTest extends FlowBoardBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl);
        assertEquals("Stats", wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Stats"))).getText());
        assertEquals("10", driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Total cards'])[1]/following::span[1]")).getText());
        assertEquals("v1.0", driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='·'])[2]/following::span[1]")).getText());
    }
}
