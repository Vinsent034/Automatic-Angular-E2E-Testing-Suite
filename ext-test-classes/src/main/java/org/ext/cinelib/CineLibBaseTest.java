package org.ext.cinelib;

import org.ext.WebDriverFactory;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.Assert.fail;

/**
 * Base test per gli scenari CineLib. A differenza di {@link org.ext.BaseTest} (Spotify,
 * porta 4200, richiede login OAuth), CineLib gira su dati locali: niente autenticazione,
 * porta 4300. Riusa lo stesso {@link WebDriverFactory} (driver Chrome condiviso).
 */
public abstract class CineLibBaseTest {
    public abstract String getLocator();

    public WebDriver driver;
    public String baseUrl;
    public StringBuffer verificationErrors = new StringBuffer();
    public WebDriverWait wait;

    @Before
    public final void setUp() {
        baseUrl = "http://localhost:4300/";
        driver = WebDriverFactory.getDriver();
        if (driver == null) {
            WebDriverFactory.init();
            driver = WebDriverFactory.getDriver();
        }
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.get(baseUrl);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
    }

    @After
    public final void tearDown() {
        String verificationErrorString = verificationErrors.toString();
        if (!verificationErrorString.isEmpty()) {
            fail(verificationErrorString);
        }
    }
}
