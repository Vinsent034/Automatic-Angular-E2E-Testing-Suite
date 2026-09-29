package org.ext.flowboard.s6;

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
        driver.get(baseUrl);
        assertEquals("Stats", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/app-header[1]/header[1]/div[1]/nav[1]/a[2]"))).getText());
        assertEquals("10", driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/app-header[1]/header[1]/div[1]/div[2]/span[2]")).getText());
        assertEquals("v1.0", driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/footer[1]/div[1]/span[5]")).getText());
    }
}
