package org.ext.flowboard.s2;

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
        driver.get(baseUrl);
        assertEquals("4", wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".column:nth-child(1) > .column-header > .badge"))).getText());
        driver.findElement(By.cssSelector(".column:nth-child(1) .card-slot:nth-child(1) .input"));
        driver.findElement(By.cssSelector(".column:nth-child(2)"));
    }
}
