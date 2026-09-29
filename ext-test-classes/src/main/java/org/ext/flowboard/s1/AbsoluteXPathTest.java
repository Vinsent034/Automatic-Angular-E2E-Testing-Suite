package org.ext.flowboard.s1;

import org.ext.flowboard.FlowBoardBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.assertEquals;

public class AbsoluteXPathTest extends FlowBoardBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl);
        WebElement s = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-board[1]/section[1]/div[2]/div[1]/input[1]")));
        s.clear(); s.sendKeys("login"); Thread.sleep(700);
        driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-board[1]/section[1]/div[3]/section[2]/app-column[1]/ul[1]/li[1]/app-card[1]/article[1]"));
        assertEquals("Design login", driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-board[1]/section[1]/div[3]/section[2]/app-column[1]/ul[1]/li[1]/app-card[1]/article[1]/a[1]/h4[1]")).getText());
    }
}
