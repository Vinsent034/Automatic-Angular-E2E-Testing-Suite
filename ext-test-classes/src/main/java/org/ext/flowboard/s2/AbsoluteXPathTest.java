package org.ext.flowboard.s2;

import org.ext.flowboard.FlowBoardBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertEquals;

public class AbsoluteXPathTest extends FlowBoardBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl);
        assertEquals("4", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-board[1]/section[1]/div[3]/section[1]/header[1]/span[1]"))).getText());
        driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-board[1]/section[1]/div[3]/section[1]/app-column[1]/ul[1]/li[1]/app-card[1]/article[1]/footer[1]/label[1]/select[1]"));
        driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-board[1]/section[1]/div[3]/section[2]"));
    }
}
