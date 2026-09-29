package org.ext.flowboard.s3;

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
        driver.get(baseUrl + "card/new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("field-title")));
        driver.findElement(By.id("field-priority"));
        assertEquals("Save card", driver.findElement(By.cssSelector(".submit-btn")).getText());
    }
}
