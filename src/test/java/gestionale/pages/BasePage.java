package gestionale.pages;

import gestionale.support.Config;
import gestionale.support.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

// "abstract": non si usa da sola, la estendono LoginPage, ProdottiPage, ecc.
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(Config.TIMEOUT_SECONDI));
    }

    // Apre una pagina del gestionale, es. apri("/login")
    protected void apri(String percorso) {
        driver.get(Config.BASE_URL + percorso);
    }

    // Aspetta che l'elemento sia visibile e lo restituisce
    protected WebElement attendiVisibile(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // Click "sicuro": riprova finché non riesce o scade il timeout
    protected void clicca(By locator) {
        wait.until(d -> {
            try {
                WebElement elemento = d.findElement(locator);
                if (!elemento.isDisplayed() || !elemento.isEnabled()) {
                    return false;
                }
                elemento.click();
                return true;
            } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
                // Angular ha ridisegnato l'elemento, o qualcosa lo copre: si riprova
                return false;
            }
        });
    }

    // Svuota il campo e scrive il testo
    protected void scrivi(By locator, String testo) {
        WebElement campo = attendiVisibile(locator);
        campo.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        campo.sendKeys(testo);
    }

    protected String leggiTesto(By locator) {
        return attendiVisibile(locator).getText();
    }

    // true se l'elemento compare entro il timeout, false altrimenti
    protected boolean isVisibile(By locator) {
        try {
            attendiVisibile(locator);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected void attendiScomparsa(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected void attendiUrlContiene(String parte) {
        wait.until(ExpectedConditions.urlContains(parte));
    }

    public String urlCorrente() {
        return driver.getCurrentUrl();
    }
}