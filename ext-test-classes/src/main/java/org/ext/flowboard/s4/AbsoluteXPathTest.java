package org.ext.flowboard.s4;

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
        driver.get(baseUrl + "card/3");
        assertEquals("Design login", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-card-detail[1]/section[1]/article[1]/header[1]/h3[1]"))).getText());
        assertEquals("Marco", driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-card-detail[1]/section[1]/article[1]/app-panel[1]/section[1]/div[1]/div[1]/div[2]/span[2]")).getText());
        driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-card-detail[1]/section[1]/article[1]/footer[1]/button[1]"));
    }
}
