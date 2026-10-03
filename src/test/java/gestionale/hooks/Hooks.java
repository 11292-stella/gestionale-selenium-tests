package gestionale.hooks;

import gestionale.support.ApiClient;
import gestionale.support.ContestoTest;
import gestionale.support.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.nio.charset.StandardCharsets;

public class Hooks {

    // Una volta sola, prima di TUTTI gli scenari: crea l'utente di test se non c'è
    // (come il setup di __init__.robot)
    @BeforeAll
    public static void preparaUtenteDiTest() {
        ApiClient.registraUtenteDiTest();
    }

    // Prima di ogni scenario: apre un browser nuovo e pulito
    @Before
    public void apriBrowser() {
        DriverManager.getDriver();
    }

    // Dopo ogni scenario, in quest'ordine:
    // 1. se è fallito, screenshot e HTML (prima che la pagina cambi)
    // 2. cancella i dati creati dallo scenario
    // 3. chiude il browser
    @After
    public void chiudiBrowser(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                salvaScreenshot(scenario);
                salvaHtml(scenario);
            }
        } finally {
            try {
                ContestoTest.pulisciDatiCreati();
            } catch (Exception e) {
                // La pulizia non deve impedire la chiusura del browser
                System.err.println("Pulizia dati non riuscita: " + e.getMessage());
            } finally {
                DriverManager.chiudiDriver();
            }
        }
    }

    private void salvaScreenshot(Scenario scenario) {
        try {
            WebDriver driver = DriverManager.getDriver();
            byte[] immagine = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(immagine, "image/png", "Screenshot - " + scenario.getName());
        } catch (Exception e) {
            // Se la pagina è rotta, lo screenshot non deve far fallire anche l'hook
            System.err.println("Screenshot non riuscito: " + e.getMessage());
        }
    }

    // Come "Salva HTML Se Fallito" in Robot: il DOM già disegnato da Angular
    private void salvaHtml(Scenario scenario) {
        try {
            String html = DriverManager.getDriver().getPageSource();
            scenario.attach(html.getBytes(StandardCharsets.UTF_8), "text/html", "HTML - " + scenario.getName());
        } catch (Exception e) {
            System.err.println("Salvataggio HTML non riuscito: " + e.getMessage());
        }
    }
}