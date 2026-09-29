package org.ext.flowboard.s4;

import org.ext.flowboard.FlowBoardBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.assertEquals;

public class KatalonXPathTest extends FlowBoardBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "card/3");
        assertEquals("Design login", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='#3'])[1]/following::h3[1]"))).getText());
        assertEquals("Marco", driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Assignee'])[1]/following::span[1]")).getText());
        driver.findElement(By.xpath("//button[@type='button']"));
    }
}
