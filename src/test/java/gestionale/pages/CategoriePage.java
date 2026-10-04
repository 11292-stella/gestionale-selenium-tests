package gestionale.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;

// Titolo, Salva, Annulla, errore, notifica e confirm arrivano da FormPage
public class CategoriePage extends FormPage {

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
        return rispondiAlConfirm(conferma);
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
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String descrizioneInLista(String nome) {
        return leggiTesto(By.xpath(
                "//tr[contains(., '" + nome + "')]//td[contains(@class, 'mat-column-descrizione')]")).strip();
    }

    public String nomeInLista(String nome) {
        return leggiTesto(cellaNome(nome)).strip();
    }

    // Quanti elementi <tag> ci sono DENTRO la cella del nome (deve essere 0: niente XSS)
    public int contaTagNelNome(String nome, String tag) {
        attendiVisibile(cellaNome(nome));
        return driver.findElements(By.xpath(
                "//tr[contains(., '" + nome + "')]//td[contains(@class, 'mat-column-nome')]//" + tag)).size();
    }

    public String valoreDescrizioneNelForm() {
        return attendiVisibile(inputDescrizione).getDomProperty("value");
    }
}