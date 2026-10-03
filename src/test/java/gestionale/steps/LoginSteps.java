package gestionale.steps;

import gestionale.pages.LoginPage;
import gestionale.support.Config;
import io.cucumber.java.it.Allora;
import io.cucumber.java.it.Dato;
import io.cucumber.java.it.E;
import io.cucumber.java.it.Quando;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginSteps {

    private final LoginPage loginPage = new LoginPage();

    @Dato("che sono sulla pagina di login")
    public void cheSonoSullaPaginaDiLogin() {
        loginPage.apriPagina();
    }

    @Quando("eseguo il login con le credenziali dell'utente di test")
    public void eseguoIlLoginConLeCredenzialiDellUtenteDiTest() {
        loginPage.eseguiLogin(Config.USERNAME, Config.PASSWORD);
    }

    @Quando("eseguo il login con username {string} e password {string}")
    public void eseguoIlLoginConUsernameEPassword(String username, String password) {
        loginPage.eseguiLogin(username, password);
    }

    @Allora("vedo la dashboard")
    public void vedoLaDashboard() {
        assertTrue(loginPage.isDashboardVisibile(),
                "Dopo il login doveva comparire la dashboard (bottone Esci)");
    }

    @Allora("vedo il messaggio di errore")
    public void vedoIlMessaggioDiErrore() {
        assertTrue(loginPage.isMessaggioErroreVisibile(),
                "Doveva comparire il messaggio di errore del login");
    }

    @E("resto sulla pagina di login")
    public void restoSullaPaginaDiLogin() {
        assertTrue(loginPage.urlCorrente().contains("/login"),
                "Doveva restare su /login, invece l'URL è: " + loginPage.urlCorrente());
    }

    @Allora("il bottone Accedi è disabilitato")
    public void ilBottoneAccediEDisabilitato() {
        assertFalse(loginPage.isBottoneAccediAbilitato(),
                "Con i campi vuoti il bottone Accedi doveva essere disabilitato");
    }
}
