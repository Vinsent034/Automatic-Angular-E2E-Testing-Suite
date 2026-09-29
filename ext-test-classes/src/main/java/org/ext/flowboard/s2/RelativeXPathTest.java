package org.ext.flowboard.s2;

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
        assertEquals("4", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[normalize-space()='4']"))).getText());
        driver.findElement(By.xpath("//article[@aria-label='Card Setup repository']//select[@class='input select move-select']"));
        driver.findElement(By.xpath("//section[@aria-label='In Progress column']"));
    }
}
