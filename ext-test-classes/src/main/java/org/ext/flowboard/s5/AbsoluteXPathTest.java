package org.ext.flowboard.s5;

import org.ext.flowboard.FlowBoardBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.assertEquals;

public class AbsoluteXPathTest extends FlowBoardBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "stats");
        assertEquals("10", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-stats[1]/section[1]/div[2]/div[1]/span[2]"))).getText());
        assertEquals("4", driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-stats[1]/section[1]/div[2]/div[2]/span[2]")).getText());
        driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-stats[1]/section[1]/div[3]/app-panel[1]/section[1]/div[1]/table[1]/tbody[1]/tr[3]"));
    }
}
