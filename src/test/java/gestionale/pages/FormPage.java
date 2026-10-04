package gestionale.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

// Le parti comuni a tutti i form del gestionale (categorie, prodotti, ...).
// CategoriePage e ProdottiPage la estendono; CommonSteps la usa direttamente.
public class FormPage extends BasePage {

    protected final By titoloPagina = By.cssSelector("mat-card h2");
    protected final By erroreForm = By.cssSelector(".stato-errore");
    protected final By bottoneSalva = bottone("Salva");
    protected final By bottoneAnnulla = bottone("Annulla");

    public String titolo() {
        return leggiTesto(titoloPagina);
    }

    public String erroreNelForm() {
        return leggiTesto(erroreForm);
    }

    public boolean isSalvaAbilitato() {
        return attendiVisibile(bottoneSalva).isEnabled();
    }

    // Solo il click: chi deve aspettare il ritorno alla lista lo fa nella sua pagina
    public void cliccaSalva() {
        clicca(bottoneSalva);
    }

    // Scrive in un campo cercandolo per etichetta, es. scriviNelCampo("Prezzo", "-5")
    public void scriviNelCampo(String etichetta, String testo) {
        scrivi(campo(etichetta), testo);
    }

    // Valore attuale di un campo (per gli input si usa la proprietà "value", non getText)
    public String valoreCampo(String etichetta) {
        return attendiVisibile(campo(etichetta)).getDomProperty("value");
    }

    // Notifica temporanea di Angular Material (MatSnackBar)
    public boolean isNotificaVisibile(String testo) {
        return isVisibile(By.xpath("//mat-snack-bar-container[contains(., '" + testo + "')]"));
    }

    // Risponde al confirm() del browser e restituisce il suo testo
    protected String rispondiAlConfirm(boolean conferma) {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String testo = alert.getText();
        if (conferma) {
            alert.accept();     // OK
        } else {
            alert.dismiss();    // Annulla
        }
        return testo;
    }
}