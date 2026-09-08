package org.ext;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.Assert.fail;

public abstract class BaseTest {
    public abstract String getLocator();

    public WebDriver driver;
    public String baseUrl;
    public boolean acceptNextAlert = true;
    public StringBuffer verificationErrors = new StringBuffer();
    public WebDriverWait wait;

    @Before
    public final void setUp() {
        baseUrl = "http://127.0.0.1:4200/";
        driver = WebDriverFactory.getDriver();
        if (driver == null) {
            WebDriverFactory.init();
            driver = WebDriverFactory.getDriver();
            wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            authenticate();
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

    /**
     * Il browser di test parte da un profilo pulito (non loggato a Spotify).
     * Se compare la pagina di login, l'utente ha fino a 2 minuti per completarlo
     * manualmente; appena l'app risulta caricata e autenticata (sidebar presente)
     * l'attesa termina. Se la sessione e' gia' valida, ritorna subito.
     */
    public final void authenticate() throws RuntimeException {
        WebDriverWait loginWait = new WebDriverWait(driver, Duration.ofSeconds(25));
        driver.get(baseUrl);
        loginWait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[@ng-reflect-router-link='/search']")
        ));
    }
}
