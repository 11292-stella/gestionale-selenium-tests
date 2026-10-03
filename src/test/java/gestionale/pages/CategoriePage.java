package gestionale.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CategoriePage extends BasePage {

    // ---------------------------------------------------------------
    // Selettori (presi da categorie_page.resource)
    // ---------------------------------------------------------------

    // Lista
    private final By inputCerca = campo("Cerca categoria");
    private final By bottoneNuova = bottone("Nuova categoria");
    private final By bottoneAzzeraRicerca = By.cssSelector("button[mattooltip='Azzera ricerca']");

    // Form crea/modifica
    private final By inputNome = campo("Nome");
    private final By inputDescrizione = campo("Descrizione");
    private final By bottoneSalva = bottone("Salva");
    private final By bottoneAnnulla = bottone("Annulla");
    private final By titoloPagina = By.cssSelector("mat-card h2");
    private final By erroreForm = By.cssSelector(".stato-errore");

    // In Robot era role=textbox[name="..."]: il nome accessibile può venire
    // dalla mat-label, dal placeholder o da aria-label. Li proviamo tutti e tre.
    private static By campo(String etichetta) {
        return By.xpath("//*[self::input or self::textarea]["
                + "@placeholder='" + etichetta + "' or @aria-label='" + etichetta + "'"
                + " or ancestor::mat-form-field[.//mat-label[normalize-space()='" + etichetta + "']]]");
    }

    // In Robot era role=button[name="..."].
    // Cerca un bottone che contiene un pezzo di testo ESATTAMENTE uguale:
    // così ignora le icone Material (es. <mat-icon>add</mat-icon>)
    private static By bottone(String testo) {
        return By.xpath("//button[.//text()[normalize-space()='" + testo + "']]");
    }

    // La riga della tabella che contiene quel testo (come tr:has-text("..."))
    private static By riga(String testo) {
        return By.xpath("//tr[contains(., '" + testo + "')]");
    }

    // La cella "Nome" nella riga di quella categoria
    private static By cellaNome(String nome) {
        return By.xpath("//tr[contains(., '" + nome + "')]//td[contains(@class, 'mat-column-nome')]");
    }

    // ---------------------------------------------------------------
    // Navigazione diretta
    // ---------------------------------------------------------------

    // Apre il form di una categoria direttamente dall'URL, es. /categorie/999999
    public void apriCategoriaPerId(int id) {
        apri("/categorie/" + id);
    }

    // ---------------------------------------------------------------
    // Azioni - lista
    // ---------------------------------------------------------------

    public void cerca(String testo) {
        scrivi(inputCerca, testo);
    }

    public void azzeraRicerca() {
        clicca(bottoneAzzeraRicerca);
    }

    public void apriFormNuova() {
        clicca(bottoneNuova);
        attendiUrlContiene("/categorie/nuovo");
    }

    // Matita nella riga + attesa che il form sia popolato (caricamento asincrono)
    public void apriFormModifica(String nome) {
        clicca(By.xpath("//tr[contains(., '" + nome + "')]//button[@mattooltip='Modifica']"));
        wait.until(ExpectedConditions.urlMatches(".*/categorie/\\d+$"));
        wait.until(ExpectedConditions.attributeToBe(inputNome, "value", nome));
    }

    // Cestino + risposta al confirm() del browser. Restituisce il testo del confirm.
    public String elimina(String nome, boolean conferma) {
        clicca(By.xpath("//tr[contains(., '" + nome + "')]//button[@mattooltip='Elimina']"));

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String testo = alert.getText();
        if (conferma) {
            alert.accept();     // OK
        } else {
            alert.dismiss();    // Annulla
        }
        return testo;
    }

    // ---------------------------------------------------------------
    // Azioni - form
    // ---------------------------------------------------------------

    public void compilaForm(String nome, String descrizione) {
        scrivi(inputNome, nome);
        scrivi(inputDescrizione, descrizione);
    }

    public void svuotaNome() {
        scrivi(inputNome, "");
    }

    // Salva e aspetta il ritorno alla lista (vale per crea e modifica)
    public void salva() {
        cliccaSalva();
        wait.until(ExpectedConditions.urlMatches(".*/categorie$"));
    }

    // Solo il click: per i casi in cui il salvataggio deve fallire
    public void cliccaSalva() {
        clicca(bottoneSalva);
    }

    public void annulla() {
        clicca(bottoneAnnulla);
        wait.until(ExpectedConditions.urlMatches(".*/categorie$"));
    }

    // ---------------------------------------------------------------
    // Verifiche (restituiscono valori, le assert stanno negli steps)
    // ---------------------------------------------------------------

    public boolean isInLista(String nome) {
        return isVisibile(riga(nome));
    }

    // Aspetta che la riga sparisca: true se sparisce entro il timeout
    public boolean isNonInLista(String nome) {
        try {
            attendiScomparsa(riga(nome));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public boolean isSalvaAbilitato() {
        return attendiVisibile(bottoneSalva).isEnabled();
    }

    public String descrizioneInLista(String nome) {
        return leggiTesto(By.xpath(
                "//tr[contains(., '" + nome + "')]//td[contains(@class, 'mat-column-descrizione')]")).strip();
    }

    public String nomeInLista(String nome) {
        return leggiTesto(cellaNome(nome)).strip();
    }

    // Quanti elementi <tag> ci sono DENTRO la cella del nome.
    // Se il nome contiene "<b>Dolci</b>" e la pagina è sicura, deve essere 0:
    // il tag va mostrato come testo, non interpretato come grassetto.
    public int contaTagNelNome(String nome, String tag) {
        attendiVisibile(cellaNome(nome));
        return driver.findElements(By.xpath(
                "//tr[contains(., '" + nome + "')]//td[contains(@class, 'mat-column-nome')]//" + tag)).size();
    }

    public String valoreDescrizioneNelForm() {
        return attendiVisibile(inputDescrizione).getDomProperty("value");
    }

    public String titolo() {
        return leggiTesto(titoloPagina);
    }

    public String erroreNelForm() {
        return leggiTesto(erroreForm);
    }

    // Notifica temporanea di Angular Material (MatSnackBar), come "Verifica Notifica" in Robot
    public boolean isNotificaVisibile(String testo) {
        return isVisibile(By.xpath("//mat-snack-bar-container[contains(., '" + testo + "')]"));
    }
}