package gestionale.support;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public class DriverManager {

    // Un browser per ogni thread: serve se un giorno lanci i test in parallelo
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    // Nessuno deve fare "new DriverManager()": si usa solo con i metodi statici
    private DriverManager() {
    }

    // Restituisce il browser; se non esiste ancora, lo apre
    public static WebDriver getDriver() {
        if (driver.get() == null) {
            driver.set(creaDriver());
        }
        return driver.get();
    }

    // Chiude il browser e lo toglie dal ThreadLocal
    public static void chiudiDriver() {
        WebDriver d = driver.get();
        if (d != null) {
            d.quit();
            driver.remove();
        }
    }

    private static WebDriver creaDriver() {
        ChromeOptions options = new ChromeOptions();

        // Headless solo se lo chiedi: mvn test -Dheadless=true
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        if (headless) {
            options.addArguments("--headless=new");
        }

        options.addArguments("--window-size=1920,1080");
        options.addArguments("--lang=it-IT");

        WebDriver d = new ChromeDriver(options);

        // Niente attesa implicita: useremo solo attese esplicite nella BasePage
        d.manage().timeouts().implicitlyWait(Duration.ZERO);

        return d;
    }
}