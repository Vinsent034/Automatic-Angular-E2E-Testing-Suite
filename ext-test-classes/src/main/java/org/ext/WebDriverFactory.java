package org.ext;

import com.google.common.collect.ImmutableList;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v131.network.Network;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WebDriverFactory {
    private static ChromeDriver driver;

    private WebDriverFactory() {}

    public static void init() {
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();
            options.setAcceptInsecureCerts(true);
            options.addArguments(
                    //"--headless",
                    "--disable-gpu",
                    "--window-size=1920,1080",
                    "--no-sandbox",
                    "--ignore-certificate-errors",
                    "--allow-running-insecure-content",
                    // Disabilita la traduzione automatica di Chrome: i locatori testuali
                    // inglesi ('Search', 'Songs', ...) restano validi.
                    "--lang=en-US",
                    "--disable-features=Translate,TranslateUI",
                    // Profilo persistente: la sessione Spotify viene salvata su disco,
                    // cosi' il login va fatto una sola volta e i run successivi lo riusano.
                    "--user-data-dir=C:\\Users\\vince\\selenium-spotify-profile");

            Map<String, Object> prefs = new HashMap<>();
            prefs.put("translate.enabled", false);
            options.setExperimentalOption("prefs", prefs);

            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

            // Chiude Chrome alla fine della JVM: il profilo persistente non resta bloccato
            // e il run successivo puo' riaprirlo. La sessione Spotify resta salvata su disco.
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try { if (driver != null) driver.quit(); } catch (Exception ignored) {}
            }));

            // Blocco di Sentry tramite CDP. Non essenziale: se la versione CDP non combacia
            // con il Chrome installato, si prosegue comunque senza far fallire l'init.
            try {
                DevTools devTools = driver.getDevTools();
                devTools.createSession();
                devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
                devTools.send(Network.setBlockedURLs(ImmutableList.of("*sentry*", "*ingest.sentry.io*")));
            } catch (Exception e) {
                System.out.println("[WebDriverFactory] DevTools/sentry-block saltato: " + e.getMessage());
            }
        }
    }

    public static WebDriver getDriver() { return driver; }

    public static void quitDriver() {
        driver.quit();
        driver = null;
    }
}
