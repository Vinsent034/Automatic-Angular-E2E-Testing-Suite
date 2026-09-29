package org.ext.flowboard.s6;

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
        driver.get(baseUrl);
        assertEquals("Stats", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@routerlink='/stats']"))).getText());
        assertEquals("10", driver.findElement(By.xpath("//span[@data-kind='total']")).getText());
        assertEquals("v1.0", driver.findElement(By.xpath("//span[@class='footer-version']")).getText());
    }
}
