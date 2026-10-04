package gestionale.pages;

import gestionale.support.DatiTest;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProdottiPage extends FormPage {

    // ---------------------------------------------------------------
    // Selettori (presi da prodotti_page.resource)
    // ---------------------------------------------------------------

    // Lista
    private final By inputCerca = campo("Cerca prodotto");
    private final By bottoneNuovo = bottone("Nuovo prodotto");
    private final By bottoneAzzeraFiltri = By.cssSelector("button[mattooltip='Azzera filtri']");

    // Form
    private final By inputNome = campo("Nome");
    private final By inputDescrizione = campo("Descrizione");
    private final By inputPrezzo = campo("Prezzo");
    private final By inputCosto = campo("Costo produzione");
    private final By inputImmagine = campo("Immagine (URL)");

    // La riga della tabella che contiene quel testo (come tr:has-text("..."))
    private static String xpathRiga(String nome) {
        return "//tr[contains(., '" + nome + "')]";
    }

    private static By riga(String nome) {
        return By.xpath(xpathRiga(nome));
    }

    // Una cella della riga: colonna = nome | categoria | prezzo | costoProduzione | stato | immagine
    private static By cella(String nome, String colonna) {
        return By.xpath(xpathRiga(nome) + "//td[contains(@class, 'mat-column-" + colonna + "')]");
    }

    // ---------------------------------------------------------------
    // Navigazione diretta (dall'URL)
    // ---------------------------------------------------------------

    public void apriLista() {
        apri("/prodotti");
        attendiVisibile(inputCerca);
    }

    public void apriFormNuovoDaUrl() {
        apri("/prodotti/nuovo");
        attendiVisibile(inputNome);
    }

    public void apriProdottoPerId(int id) {
        apri("/prodotti/" + id);
    }

    // ---------------------------------------------------------------
    // Azioni - lista e filtri
    // ---------------------------------------------------------------

    public void cerca(String testo) {
        scrivi(inputCerca, testo);
    }

    public void filtraPerCategoria(String categoria) {
        selezionaDaDropdown("Categoria", categoria);
    }

    public void filtraPerStato(String stato) {
        selezionaDaDropdown("Stato", stato);
    }

    public void azzeraFiltri() {
        clicca(bottoneAzzeraFiltri);
    }

    // Il bottone compare solo se c'è almeno un filtro attivo
    public boolean isAzzeraFiltriAssente() {
        try {
            attendiScomparsa(bottoneAzzeraFiltri);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void apriFormNuovo() {
        clicca(bottoneNuovo);
        attendiUrlContiene("/prodotti/nuovo");
    }

    // Matita nella riga + attesa che Angular abbia caricato i dati nel form
    public void apriFormModifica(String nome) {
        clicca(By.xpath(xpathRiga(nome) + "//button[@mattooltip='Modifica']"));
        wait.until(ExpectedConditions.urlMatches(".*/prodotti/\\d+$"));
        wait.until(ExpectedConditions.attributeToBe(inputNome, "value", nome));
    }

    // Cestino + risposta al confirm(). Restituisce il testo del confirm.
    public String elimina(String nome, boolean conferma) {
        clicca(By.xpath(xpathRiga(nome) + "//button[@mattooltip='Elimina']"));
        return rispondiAlConfirm(conferma);
    }

    // ---------------------------------------------------------------
    // Azioni - form (crea e modifica usano lo stesso form)
    // ---------------------------------------------------------------

    public void compilaForm(DatiTest.Prodotto p) {
        scrivi(inputNome, p.nome());
        scrivi(inputDescrizione, p.descrizione());
        scrivi(inputPrezzo, String.valueOf(p.prezzo()));
        scrivi(inputCosto, String.valueOf(p.costo()));
        scrivi(inputImmagine, p.immagine());
        selezionaDaDropdown("Categoria", p.categoria());
        impostaSwitch("Attivo", p.attivo());
        impostaSwitch("Esaurito", p.esaurito());
    }

    public void scegliCategoria(String categoria) {
        selezionaDaDropdown("Categoria", categoria);
    }

    public String categoriaSelezionata() {
        return testoDropdown("Categoria");
    }

    // Salva e aspetta il ritorno alla lista (vale per crea e modifica)
    public void salva() {
        cliccaSalva();
        wait.until(ExpectedConditions.urlMatches(".*/prodotti$"));
    }

    public void doppioClickSalva() {
        doppioClick(bottoneSalva);
        wait.until(ExpectedConditions.urlMatches(".*/prodotti$"));
    }

    public void annulla() {
        clicca(bottoneAnnulla);
        wait.until(ExpectedConditions.urlMatches(".*/prodotti$"));
    }

    // ---------------------------------------------------------------
    // Verifiche (restituiscono valori, le assert stanno negli steps)
    // ---------------------------------------------------------------

    public boolean isInLista(String nome) {
        return isVisibile(riga(nome));
    }

    public boolean isNonInLista(String nome) {
        try {
            attendiScomparsa(riga(nome));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String testoCella(String nome, String colonna) {
        return leggiTesto(cella(nome, colonna)).strip();
    }

    // Prodotto senza immagine: icona placeholder e nessun <img> rotto
    public boolean isPlaceholderImmagine(String nome) {
        boolean placeholder = isVisibile(By.xpath(xpathRiga(nome)
                + "//*[contains(@class, 'prodotto-thumb') and contains(@class, 'placeholder')]"));
        int immagini = driver.findElements(By.xpath(xpathRiga(nome)
                + "//td[contains(@class, 'mat-column-immagine')]//img")).size();
        return placeholder && immagini == 0;
    }

    // Quanti elementi <tag> ci sono DENTRO la cella del nome (deve essere 0: niente XSS)
    public int contaTagNelNome(String nome, String tag) {
        attendiVisibile(cella(nome, "nome"));
        return driver.findElements(By.xpath(xpathRiga(nome)
                + "//td[contains(@class, 'mat-column-nome')]//" + tag)).size();
    }

    // Un testo qualsiasi sulla pagina, es. "Nessun prodotto corrisponde ai filtri selezionati."
    public boolean isMessaggioVisibile(String testo) {
        return isVisibile(By.xpath("//*[contains(normalize-space(text()), '" + testo + "')]"));
    }
}
