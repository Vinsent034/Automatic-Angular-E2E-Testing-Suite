package org.ext.flowboard.s5;

import org.ext.flowboard.FlowBoardBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertEquals;

public class KatalonXPathTest extends FlowBoardBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "stats");
        assertEquals("10", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Total cards'])[2]/following::span[1]"))).getText());
        assertEquals("4", driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Backlog'])[1]/following::span[1]")).getText());
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='#3'])[1]/ancestor::tr[1]"));
    }
}
