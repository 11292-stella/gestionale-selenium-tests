package gestionale.pages;

import gestionale.support.ApiClient;
import gestionale.support.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

public class DashboardPage extends BasePage {

    private final By bottoneEsci = By.xpath(
            "//*[self::button or self::a][contains(normalize-space(.), 'Esci')]");

    // Come "Apri Gestionale Da Loggato" in Robot:
    // token via API + localStorage, così si entra senza passare dal form
    public void apriDaLoggato() {
        String token = ApiClient.ottieniToken();

        // 1. Serve una pagina del gestionale aperta: il localStorage è legato al dominio
        apri("/login");

        // 2. Si mette il token dove lo cerca Angular
        ((JavascriptExecutor) driver).executeScript(
                "window.localStorage.setItem('jwt_token', arguments[0]);", token);

        // 3. Si ricarica la home: ora Angular trova il token e ci considera loggati
        apri("/");
        attendiVisibile(bottoneEsci);
    }

    // Come "Vai Alla Sezione": clicca la card e controlla l'URL
    public void vaiAllaSezione(String nomeCard, String percorso) {
        By card = By.xpath("(//*[normalize-space(text())='" + nomeCard + "'])[1]");
        clicca(card);
        attendiUrlContiene(percorso);
    }
}