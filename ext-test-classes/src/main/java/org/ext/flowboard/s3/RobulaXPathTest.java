package org.ext.flowboard.s3;

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
        driver.get(baseUrl + "card/new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='field-title']")));
        driver.findElement(By.xpath("//select[@id='field-priority']"));
        assertEquals("Save card", driver.findElement(By.xpath("//button")).getText());
    }
}
