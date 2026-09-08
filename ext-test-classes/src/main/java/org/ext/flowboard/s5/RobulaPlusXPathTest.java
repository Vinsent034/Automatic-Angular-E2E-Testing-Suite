package org.ext.flowboard.s5;

import org.ext.flowboard.FlowBoardBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertEquals;

public class RobulaPlusXPathTest extends FlowBoardBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "stats");
        assertEquals("10", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@data-stat='total']/*[contains(text(),'10')]"))).getText());
        assertEquals("4", driver.findElement(By.xpath("//*[@data-stat='backlog']/*[contains(text(),'4')]")).getText());
        driver.findElement(By.xpath("//*[@data-card-id='3']"));
    }
}
