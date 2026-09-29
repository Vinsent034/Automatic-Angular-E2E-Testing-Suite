package org.ext.flowboard.s2;

import org.ext.flowboard.FlowBoardBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.assertEquals;

public class HookXPathTest extends FlowBoardBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl);
        assertEquals("4", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@x-test-column-count='backlog']"))).getText());
        driver.findElement(By.xpath("//*[@x-test-card-move='1']"));
        driver.findElement(By.xpath("//*[@x-test-column='inprogress']"));
    }
}
