package org.ext.flowboard.s2;

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
        assertEquals("4", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Backlog'])[1]/following::span[1]"))).getText());
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Move'])[1]/following::select[1]"));
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Delete'])[4]/following::section[1]"));
    }
}
