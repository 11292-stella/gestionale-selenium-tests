package gestionale.steps;

import gestionale.pages.ProdottiPage;
import gestionale.support.ApiClient;
import gestionale.support.ContestoTest;
import gestionale.support.DatiTest;
import io.cucumber.java.it.Allora;
import io.cucumber.java.it.Dato;
import io.cucumber.java.it.E;
import io.cucumber.java.it.Quando;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProdottiSteps {

    private final ProdottiPage prodotti = new ProdottiPage();
    private final ContestoTest ctx = ContestoTest.get();

    // ---------------------------------------------------------------
    // Dati preparati via API (Arrange)
    // ---------------------------------------------------------------

    @Dato("che esiste un prodotto creato via API")
    public void cheEsisteUnProdotto() {
        creaViaApi("Prodotto Selenium", true, false, 5.5, 2.0, ApiClient.IMMAGINE_PREDEFINITA);
    }

    @Dato("che esiste un prodotto esaurito creato via API")
    public void cheEsisteUnProdottoEsaurito() {
        creaViaApi("Esaurito Selenium", true, true, 5.5, 2.0, ApiClient.IMMAGINE_PREDEFINITA);
    }

    @Dato("che esiste un prodotto disattivo creato via API")
    public void cheEsisteUnProdottoDisattivo() {
        creaViaApi("Disattivo Selenium", false, false, 5.5, 2.0, ApiClient.IMMAGINE_PREDEFINITA);
    }

    @Dato("che esiste un prodotto disattivo ed esaurito creato via API")
    public void cheEsisteUnProdottoDisattivoEdEsaurito() {
        creaViaApi("Badge Selenium", false, true, 5.5, 2.0, ApiClient.IMMAGINE_PREDEFINITA);
    }

    @Dato("che esiste un prodotto senza immagine creato via API")
    public void cheEsisteUnProdottoSenzaImmagine() {
        creaViaApi("Senza Foto Selenium", true, false, 5.5, 2.0, null);
    }

    @Dato("che esiste un prodotto con HTML nel nome creato via API")
    public void cheEsisteUnProdottoConHtml() {
        creaViaApi("<b>Grassetto</b><img src=x onerror=alert(1)>", true, false, 5.5, 2.0,
                ApiClient.IMMAGINE_PREDEFINITA);
    }

    // {word} e non {double}: con "# language: it" Cucumber leggerebbe i decimali
    // all'italiana (7,25). Prendiamo il testo e lo convertiamo noi.
    @Dato("che esiste un prodotto esaurito con prezzo {word} e costo {word} creato via API")
    public void cheEsisteUnProdottoEsauritoConPrezzo(String prezzo, String costo) {
        creaViaApi("Precompilato Selenium", true, true,
                Double.parseDouble(prezzo), Double.parseDouble(costo), ApiClient.IMMAGINE_PREDEFINITA);
    }

    @Dato("che esistono un prodotto attivo e uno esaurito nella stessa categoria creata via API")
    public void cheEsistonoUnProdottoAttivoEUnoEsaurito() {
        creaCategoriaDiTest();
        ctx.nomeProdotto = DatiTest.nomeUnivoco("Filtro Attivo");
        ctx.prodottoId = ApiClient.creaProdotto(ctx.nomeProdotto, ctx.categoriaId);
        ctx.nomeProdotto2 = DatiTest.nomeUnivoco("Filtro Esaurito");
        ctx.prodottoId2 = ApiClient.creaProdotto(ctx.nomeProdotto2, ctx.categoriaId,
                true, true, 5.5, 2.0, ApiClient.IMMAGINE_PREDEFINITA);
    }

    @E("esiste un'altra categoria creata via API")
    public void esisteUnAltraCategoria() {
        ctx.nomeCategoria2 = DatiTest.nomeUnivoco("Cat Arrivo");
        ctx.categoriaId2 = ApiClient.creaCategoria(ctx.nomeCategoria2, "Creata dai test Selenium");
    }

    private void creaCategoriaDiTest() {
        ctx.nomeCategoria = DatiTest.nomeUnivoco("Cat Selenium");
        ctx.categoriaId = ApiClient.creaCategoria(ctx.nomeCategoria, "Creata dai test Selenium");
    }

    // Ogni prodotto di test ha la sua categoria usa e getta: mai toccare il menu base
    private void creaViaApi(String prefisso, boolean attivo, boolean esaurito,
                            double prezzo, double costo, String immagine) {
        creaCategoriaDiTest();
        ctx.nomeProdotto = DatiTest.nomeUnivoco(prefisso);
        ctx.prodottoId = ApiClient.creaProdotto(ctx.nomeProdotto, ctx.categoriaId,
                attivo, esaurito, prezzo, costo, immagine);
    }

    // ---------------------------------------------------------------
    // Navigazione diretta
    // ---------------------------------------------------------------

    @E("apro la lista prodotti")
    public void aproLaListaProdotti() {
        prodotti.apriLista();
    }

    @Quando("apro direttamente il form nuovo prodotto")
    public void aproDirettamenteIlFormNuovoProdotto() {
        prodotti.apriFormNuovoDaUrl();
    }

    @Quando("apro il prodotto con id {int}")
    public void aproIlProdottoConId(int id) {
        prodotti.apriProdottoPerId(id);
    }

    // ---------------------------------------------------------------
    // Ricerca e filtri
    // ---------------------------------------------------------------

    @E("cerco il prodotto {string}")
    public void cercoIlProdotto(String testo) {
        prodotti.cerca(testo);
    }

    @E("cerco il prodotto creato")
    public void cercoIlProdottoCreato() {
        prodotti.cerca(ctx.nomeProdotto);
    }

    @E("cerco il prodotto attivo")
    public void cercoIlProdottoAttivo() {
        prodotti.cerca(ctx.nomeProdotto);
    }

    @E("cerco il nome precedente del prodotto")
    public void cercoIlNomePrecedente() {
        prodotti.cerca(ctx.nomeProdottoPrecedente);
    }

    @E("filtro per categoria {string}")
    public void filtroPerCategoria(String categoria) {
        prodotti.filtraPerCategoria(categoria);
    }

    @E("filtro per la categoria creata")
    public void filtroPerLaCategoriaCreata() {
        prodotti.filtraPerCategoria(ctx.nomeCategoria);
    }

    @E("filtro per stato {string}")
    public void filtroPerStato(String stato) {
        prodotti.filtraPerStato(stato);
    }

    @E("azzero i filtri")
    public void azzeroIFiltri() {
        prodotti.azzeraFiltri();
    }

    // ---------------------------------------------------------------
    // Verifiche sulla lista
    // ---------------------------------------------------------------

    @Allora("vedo in lista i prodotti:")
    public void vedoInListaIProdotti(List<String> nomi) {
        for (String nome : nomi) {
            vedoIlProdottoInLista(nome);
        }
    }

    @Allora("vedo il prodotto {string} in lista")
    public void vedoIlProdottoInLista(String nome) {
        assertTrue(prodotti.isInLista(nome), "Il prodotto '" + nome + "' doveva essere in lista");
    }

    @E("non vedo il prodotto {string} in lista")
    public void nonVedoIlProdottoInLista(String nome) {
        assertTrue(prodotti.isNonInLista(nome), "Il prodotto '" + nome + "' NON doveva essere in lista");
    }

    @Allora("vedo il prodotto creato in lista")
    public void vedoIlProdottoCreatoInLista() {
        vedoIlProdottoInLista(ctx.nomeProdotto);
    }

    @Allora("non vedo il prodotto creato in lista")
    public void nonVedoIlProdottoCreatoInLista() {
        nonVedoIlProdottoInLista(ctx.nomeProdotto);
    }

    @Allora("vedo il prodotto esaurito in lista")
    public void vedoIlProdottoEsauritoInLista() {
        vedoIlProdottoInLista(ctx.nomeProdotto2);
    }

    @E("non vedo il prodotto attivo in lista")
    public void nonVedoIlProdottoAttivoInLista() {
        nonVedoIlProdottoInLista(ctx.nomeProdotto);
    }

    @Allora("non vedo il nome precedente del prodotto in lista")
    public void nonVedoIlNomePrecedente() {
        nonVedoIlProdottoInLista(ctx.nomeProdottoPrecedente);
    }

    @Allora("vedo il messaggio {string}")
    public void vedoIlMessaggio(String testo) {
        assertTrue(prodotti.isMessaggioVisibile(testo), "Doveva comparire il messaggio: " + testo);
    }

    @Allora("il bottone Azzera filtri non c'è")
    public void ilBottoneAzzeraFiltriNonCe() {
        assertTrue(prodotti.isAzzeraFiltriAssente(), "Senza filtri attivi il bottone Azzera filtri non doveva esserci");
    }

    @Allora("lo stato del prodotto creato è {string}")
    public void loStatoDelProdottoCreatoE(String stato) {
        assertEquals(stato, prodotti.testoCella(ctx.nomeProdotto, "stato"));
    }

    // DataTable a due colonne: Cucumber la trasforma in Map (colonna -> valore atteso)
    @Allora("nella riga del prodotto creato vedo:")
    public void nellaRigaDelProdottoCreatoVedo(Map<String, String> attesi) {
        attesi.forEach((colonna, atteso) ->
                assertEquals(atteso, prodotti.testoCella(ctx.nomeProdotto, colonna),
                        "Colonna '" + colonna + "' del prodotto"));
    }

    @Allora("la categoria del prodotto creato in lista è quella creata")
    public void laCategoriaInListaEQuellaCreata() {
        assertEquals(ctx.nomeCategoria, prodotti.testoCella(ctx.nomeProdotto, "categoria"));
    }

    @Allora("la categoria del prodotto creato in lista è l'altra categoria")
    public void laCategoriaInListaELAltraCategoria() {
        assertEquals(ctx.nomeCategoria2, prodotti.testoCella(ctx.nomeProdotto, "categoria"));
    }

    @Allora("il prodotto creato mostra il placeholder al posto dell'immagine")
    public void ilProdottoCreatoMostraIlPlaceholder() {
        assertTrue(prodotti.isPlaceholderImmagine(ctx.nomeProdotto),
                "Senza immagine doveva comparire il placeholder, senza <img> rotti");
    }

    @Allora("il nome del prodotto in lista è identico a quello salvato")
    public void ilNomeDelProdottoInListaEIdentico() {
        assertEquals(ctx.nomeProdotto, prodotti.testoCella(ctx.nomeProdotto, "nome"));
    }

    @E("nella cella del nome del prodotto non ci sono tag {string}")
    public void nellaCellaDelNomeDelProdottoNonCiSonoTag(String tag) {
        assertEquals(0, prodotti.contaTagNelNome(ctx.nomeProdotto, tag),
                "Il tag <" + tag + "> doveva essere mostrato come testo, non interpretato come HTML");
    }

    // ---------------------------------------------------------------
    // Form
    // ---------------------------------------------------------------

    @Quando("apro il form nuovo prodotto")
    public void aproIlFormNuovoProdotto() {
        prodotti.apriFormNuovo();
    }

    @E("apro il form di modifica del prodotto creato")
    public void aproIlFormDiModificaDelProdottoCreato() {
        prodotti.apriFormModifica(ctx.nomeProdotto);
    }

    @E("compilo il form del prodotto con dati validi")
    public void compiloIlFormDelProdottoConDatiValidi() {
        DatiTest.Prodotto p = DatiTest.generaProdotto();
        preparaProdottoDaUi(p);
        prodotti.compilaForm(p);
    }

    @E("creo un nuovo prodotto dal form")
    public void creoUnNuovoProdottoDalForm() {
        creaDalForm(DatiTest.generaProdotto());
    }

    @E("creo un nuovo prodotto dal form senza descrizione")
    public void creoUnNuovoProdottoDalFormSenzaDescrizione() {
        creaDalForm(DatiTest.generaProdotto().senzaDescrizione());
    }

    @E("creo un nuovo prodotto dal form con nome {string}")
    public void creoUnNuovoProdottoDalFormConNome(String prefisso) {
        creaDalForm(DatiTest.generaProdotto().conNome(DatiTest.nomeUnivoco(prefisso)));
    }

    @E("compilo il form di un nuovo prodotto e annullo")
    public void compiloIlFormDiUnNuovoProdottoEAnnullo() {
        DatiTest.Prodotto p = DatiTest.generaProdotto();
        preparaProdottoDaUi(p);
        prodotti.apriFormNuovo();
        prodotti.compilaForm(p);
        prodotti.annulla();
    }

    @E("faccio doppio click su Salva per un nuovo prodotto")
    public void faccioDoppioClickSuSalva() throws InterruptedException {
        DatiTest.Prodotto p = DatiTest.generaProdotto();
        preparaProdottoDaUi(p);
        prodotti.apriFormNuovo();
        prodotti.compilaForm(p);
        prodotti.doppioClickSalva();
        // Unica attesa fissa del progetto, ed è voluta: per dimostrare che una seconda
        // POST NON è partita bisogna lasciarle il tempo di arrivare, poi contare nel backend.
        Thread.sleep(2000);
    }

    @E("modifico il prodotto con nuovi dati")
    public void modificoIlProdottoConNuoviDati() {
        DatiTest.Prodotto nuovi = DatiTest.generaProdotto();
        ctx.nomeProdottoPrecedente = ctx.nomeProdotto;

        prodotti.apriFormModifica(ctx.nomeProdotto);
        prodotti.compilaForm(nuovi);
        prodotti.salva();

        // Da qui in poi "il prodotto creato" è quello con il nuovo nome
        ctx.nomeProdotto = nuovi.nome();
        ctx.prodotto = nuovi;
    }

    @E("scelgo l'altra categoria nel form")
    public void scelgoLAltraCategoriaNelForm() {
        prodotti.scegliCategoria(ctx.nomeCategoria2);
    }

    @E("scelgo la categoria creata nel form")
    public void scelgoLaCategoriaCreataNelForm() {
        prodotti.scegliCategoria(ctx.nomeCategoria);
    }

    @E("salvo il prodotto")
    public void salvoIlProdotto() {
        prodotti.salva();
    }

    @E("annullo il form del prodotto")
    public void annulloIlFormDelProdotto() {
        prodotti.annulla();
    }

    // DataTable: etichetta del campo -> numero atteso (confronto numerico: "3.1" == "3.10")
    @Allora("il form del prodotto contiene:")
    public void ilFormDelProdottoContiene(Map<String, String> attesi) {
        attesi.forEach((etichetta, atteso) ->
                assertEquals(Double.parseDouble(atteso), Double.parseDouble(prodotti.valoreCampo(etichetta)), 0.001,
                        "Campo '" + etichetta + "' del form"));
    }

    @E("nel form la categoria selezionata è quella creata")
    public void nelFormLaCategoriaSelezionataEQuellaCreata() {
        assertEquals(ctx.nomeCategoria, prodotti.categoriaSelezionata());
    }

    @E("lo switch {string} è acceso")
    public void loSwitchEAcceso(String etichetta) {
        assertTrue(prodotti.isSwitchAcceso(etichetta), "Lo switch '" + etichetta + "' doveva essere acceso");
    }

    @E("lo switch {string} è spento")
    public void loSwitchESpento(String etichetta) {
        assertFalse(prodotti.isSwitchAcceso(etichetta), "Lo switch '" + etichetta + "' doveva essere spento");
    }

    private void creaDalForm(DatiTest.Prodotto p) {
        preparaProdottoDaUi(p);
        prodotti.apriFormNuovo();
        prodotti.compilaForm(p);
        prodotti.salva();
    }

    // Il nome va salvato PRIMA di salvare: se il test si rompe a metà, l'@After lo cancella
    private void preparaProdottoDaUi(DatiTest.Prodotto p) {
        ctx.prodotto = p;
        ctx.nomeProdotto = p.nome();
        ctx.nomeProdottoCreatoDaUi = p.nome();
    }

    // ---------------------------------------------------------------
    // Eliminazione
    // ---------------------------------------------------------------

    @E("elimino il prodotto confermando")
    public void eliminoIlProdottoConfermando() {
        ctx.testoConferma = prodotti.elimina(ctx.nomeProdotto, true);
    }

    @E("elimino il prodotto annullando la conferma")
    public void eliminoIlProdottoAnnullandoLaConferma() {
        ctx.testoConferma = prodotti.elimina(ctx.nomeProdotto, false);
    }

    // ---------------------------------------------------------------
    // Verifiche nel backend (API)
    // ---------------------------------------------------------------

    @E("nel backend esiste un solo prodotto con quel nome")
    public void nelBackendEsisteUnSoloProdotto() {
        assertEquals(1, ApiClient.contaProdottiConNome(ctx.nomeProdotto),
                "Nel backend doveva esserci un solo prodotto '" + ctx.nomeProdotto + "'");
    }

    @E("nel backend il prodotto ha il nuovo nome e il nuovo prezzo")
    public void nelBackendIlProdottoHaIlNuovoNomeEPrezzo() {
        Response risposta = ApiClient.leggiProdotto(ctx.prodottoId);
        assertEquals(200, risposta.statusCode());
        assertEquals(ctx.nomeProdotto, risposta.jsonPath().getString("nome"));
        assertEquals(ctx.prodotto.prezzo(), risposta.jsonPath().getDouble("prezzo"), 0.001);
    }

    @Allora("nel backend il prodotto ha ancora il nome originale")
    public void nelBackendIlProdottoHaAncoraIlNomeOriginale() {
        assertEquals(ctx.nomeProdotto, ApiClient.leggiProdotto(ctx.prodottoId).jsonPath().getString("nome"));
    }

    @E("nel backend il prodotto non esiste più")
    public void nelBackendIlProdottoNonEsistePiu() {
        assertEquals(404, ApiClient.leggiProdotto(ctx.prodottoId).statusCode(),
                "Il prodotto doveva essere cancellato dal backend");
    }

    // "nel backend il prodotto esiste ancora" c'è già in CategorieSteps: si riusa quello
}