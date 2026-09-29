package org.ext.flowboard.s5;

import org.ext.flowboard.FlowBoardBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.assertEquals;

public class RobulaXPathTest extends FlowBoardBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "stats");
        assertEquals("10", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@data-stat='total']/span[@class='stat-value']"))).getText());
        assertEquals("4", driver.findElement(By.xpath("//div[@data-stat='backlog']/span[@class='stat-value']")).getText());
        driver.findElement(By.xpath("//tr[@data-card-id='3']"));
    }
}
