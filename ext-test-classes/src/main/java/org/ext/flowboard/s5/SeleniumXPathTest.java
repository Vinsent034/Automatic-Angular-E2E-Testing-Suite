package org.ext.flowboard.s5;

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
        driver.get(baseUrl + "stats");
        assertEquals("10", wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".stat-card:nth-child(1) > .stat-value"))).getText());
        assertEquals("4", driver.findElement(By.cssSelector(".stat-card:nth-child(2) > .stat-value")).getText());
        driver.findElement(By.cssSelector(".stats-row:nth-child(3)"));
    }
}
