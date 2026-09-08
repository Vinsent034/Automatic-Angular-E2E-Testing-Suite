package org.ext.flowboard.s5;

import org.ext.flowboard.FlowBoardBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertEquals;

public class RelativeXPathTest extends FlowBoardBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "stats");
        assertEquals("10", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='stat-value'][normalize-space()='10']"))).getText());
        assertEquals("4", driver.findElement(By.xpath("//div[@data-stat='backlog']//span[@class='stat-value'][normalize-space()='4']")).getText());
        driver.findElement(By.xpath("//tr[@data-card-id='3']"));
    }
}
