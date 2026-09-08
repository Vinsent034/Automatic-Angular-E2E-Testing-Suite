package org.ext.flowboard.s2;

import org.ext.flowboard.FlowBoardBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertEquals;

public class RobulaPlusXPathTest extends FlowBoardBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl);
        assertEquals("4", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@data-column='backlog' and @class='badge column-count']"))).getText());
        driver.findElement(By.xpath("//*[@data-card-id='1']/*/*/select"));
        driver.findElement(By.xpath("//*[@aria-label='In Progress column']"));
    }
}
