package gestionale.pages;

import org.openqa.selenium.By;

public class LoginPage extends BasePage {

    // Stessi selettori del progetto Robot (formControlName -> attributo "formcontrolname")
    private final By inputUsername = By.cssSelector("input[formcontrolname='username']");
    private final By inputPassword = By.cssSelector("input[formcontrolname='password']");
    private final By bottoneAccedi = By.cssSelector("button[type='submit']");
    private final By messaggioErrore = By.cssSelector("p.errore");

    // In Robot era "text=Esci": Selenium non ha i selettori di testo, si usa XPath
    private final By bottoneEsci = By.xpath(
            "//*[self::button or self::a][contains(normalize-space(.), 'Esci')]");

    public void apriPagina() {
        apri("/login");
        attendiVisibile(inputUsername);
    }

    public void eseguiLogin(String username, String password) {
        scrivi(inputUsername, username);
        scrivi(inputPassword, password);
        clicca(bottoneAccedi);
    }

    public boolean isDashboardVisibile() {
        return isVisibile(bottoneEsci) && !urlCorrente().contains("/login");
    }

    public boolean isMessaggioErroreVisibile() {
        return isVisibile(messaggioErrore);
    }

    public boolean isBottoneAccediAbilitato() {
        return attendiVisibile(bottoneAccedi).isEnabled();
    }
}