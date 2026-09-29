package org.ext.cookbook;

import org.ext.WebDriverFactory;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static org.junit.Assert.fail;

public abstract class CookBookBaseTest {
    public WebDriver driver;
    public String baseUrl;
    public StringBuffer verificationErrors = new StringBuffer();
    public WebDriverWait wait;

    @Before
    public final void setUp() {
        baseUrl = "http://localhost:4500/";
        driver = WebDriverFactory.getDriver();
        if (driver == null) { WebDriverFactory.init(); driver = WebDriverFactory.getDriver(); }
        wait = new WebDriverWait(driver, Duration.ofSeconds(6));
        driver.get(baseUrl);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
    }

    @After
    public final void tearDown() {
        String s = verificationErrors.toString();
        if (!s.isEmpty()) fail(s);
    }
}
