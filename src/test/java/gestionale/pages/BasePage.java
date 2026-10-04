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
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

// "abstract": non si usa da sola, la estendono tutte le pagine
public abstract class BasePage {

    // Per i confronti senza maiuscole in XPath 1.0 (che non ha lower-case())
    private static final String MAIUSCOLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String MINUSCOLE = "abcdefghijklmnopqrstuvwxyz";

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(Config.TIMEOUT_SECONDI));
    }

    // ---------------------------------------------------------------
    // Selettori riusabili per Angular Material
    // ---------------------------------------------------------------

    // In Robot era role=textbox[name="..."]: il nome accessibile può venire
    // dalla mat-label, dal placeholder o da aria-label. Li proviamo tutti e tre.
    protected static By campo(String etichetta) {
        return By.xpath("//*[self::input or self::textarea]["
                + "@placeholder='" + etichetta + "' or @aria-label='" + etichetta + "'"
                + " or ancestor::mat-form-field[.//mat-label[normalize-space()='" + etichetta + "']]]");
    }

    // In Robot era role=button[name="..."]: cerca un pezzo di testo ESATTAMENTE uguale,
    // così ignora le icone Material (es. <mat-icon>add</mat-icon>)
    protected static By bottone(String testo) {
        return By.xpath("//button[.//text()[normalize-space()='" + testo + "']]");
    }

    // Il mat-form-field che contiene quel testo, senza distinguere maiuscole
    // (come mat-form-field:has-text("...") in Robot)
    private static String xpathDropdown(String etichetta) {
        return "(//mat-form-field[contains(translate(normalize-space(.), '" + MAIUSCOLE + "', '" + MINUSCOLE + "'), '"
                + etichetta.toLowerCase() + "')])[1]";
    }

    // Una voce del menu a tendina Material
    private static By opzione(String testo) {
        return By.xpath("//mat-option[.//text()[normalize-space()='" + testo + "']]");
    }

    // Lo switch Material (mat-slide-toggle): l'elemento cliccabile ha role="switch"
    private static By interruttore(String etichetta) {
        return By.xpath("//mat-slide-toggle[contains(normalize-space(.), '" + etichetta + "')]//*[@role='switch']");
    }

    // ---------------------------------------------------------------
    // Azioni di base
    // ---------------------------------------------------------------

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

    protected void doppioClick(By locator) {
        WebElement elemento = wait.until(ExpectedConditions.elementToBeClickable(locator));
        new Actions(driver).doubleClick(elemento).perform();
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

    // ---------------------------------------------------------------
    // Componenti Angular Material
    // ---------------------------------------------------------------

    // Come "Seleziona Da Dropdown" in Robot: si clicca il mat-form-field, poi la voce
    protected void selezionaDaDropdown(String etichetta, String voce) {
        clicca(By.xpath(xpathDropdown(etichetta)));
        clicca(opzione(voce));
        // si aspetta che il pannello si chiuda, altrimenti copre il click successivo
        attendiScomparsa(By.cssSelector("mat-option"));
    }

    // Il testo della voce selezionata nel menu a tendina
    protected String testoDropdown(String etichetta) {
        return leggiTesto(By.xpath(xpathDropdown(etichetta)
                + "//*[contains(@class, 'mat-mdc-select-value-text')]")).strip();
    }

    public boolean isSwitchAcceso(String etichetta) {
        return "true".equals(attendiVisibile(interruttore(etichetta)).getDomAttribute("aria-checked"));
    }

    // Come "Imposta Switch" in Robot: clicca SOLO se serve (un click lo inverte)
    protected void impostaSwitch(String etichetta, boolean acceso) {
        if (isSwitchAcceso(etichetta) != acceso) {
            clicca(interruttore(etichetta));
            wait.until(ExpectedConditions.attributeToBe(
                    interruttore(etichetta), "aria-checked", String.valueOf(acceso)));
        }
    }
}