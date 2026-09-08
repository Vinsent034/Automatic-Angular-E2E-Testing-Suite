package org.ext.flowboard.s3;

import org.ext.flowboard.FlowBoardBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertEquals;

public class AbsoluteXPathTest extends FlowBoardBaseTest {
    @Test
    public void test() throws Exception {
        driver.get(baseUrl + "card/new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-card-form[1]/section[1]/form[1]/div[1]/input[1]")));
        driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-card-form[1]/section[1]/form[1]/div[3]/div[1]/select[1]"));
        assertEquals("Save card", driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/main[1]/app-card-form[1]/section[1]/form[1]/div[5]/button[1]")).getText());
    }
}
