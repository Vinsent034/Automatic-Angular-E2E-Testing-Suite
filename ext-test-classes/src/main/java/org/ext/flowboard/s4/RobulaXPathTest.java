package org.ext.flowboard.s4;

import org.ext.flowboard.FlowBoardBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertEquals;

public class RobulaXPathTest extends FlowBoardBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "card/3");
        assertEquals("Design login", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3"))).getText());
        assertEquals("Marco", driver.findElement(By.xpath("//span[@class='detail-assignee']")).getText());
        driver.findElement(By.xpath("//button"));
    }
}
