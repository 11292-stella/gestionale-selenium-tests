
package gestionale.steps;

import gestionale.pages.CategoriePage;
import gestionale.support.ApiClient;
import gestionale.support.ContestoTest;
import gestionale.support.DatiTest;
import io.cucumber.java.it.Allora;
import io.cucumber.java.it.Dato;
import io.cucumber.java.it.E;
import io.cucumber.java.it.Quando;
import io.restassured.response.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CategorieSteps {

    private final CategoriePage categorie = new CategoriePage();
    private final ContestoTest ctx = ContestoTest.get();

    // ---------------------------------------------------------------
    // Dati preparati via API (Arrange)
    // ---------------------------------------------------------------

    @Dato("che esiste una categoria creata via API")
    public void cheEsisteUnaCategoriaCreataViaApi() {
        creaCategoriaViaApi(DatiTest.nomeUnivoco("Cat Selenium"));
    }

    @Dato("che esiste una categoria con un prodotto creata via API")
    public void cheEsisteUnaCategoriaConUnProdottoCreataViaApi() {
        cheEsisteUnaCategoriaCreataViaApi();
        ctx.nomeProdotto = "Prodotto di " + ctx.nomeCategoria;
        ctx.prodottoId = ApiClient.creaProdotto(ctx.nomeProdotto, ctx.categoriaId);
    }

    @Dato("che esiste una categoria con accenti e HTML nel nome creata via API")
    public void cheEsisteUnaCategoriaConAccentiEHtml() {
        creaCategoriaViaApi(DatiTest.nomeUnivoco("Caffè & <b>Dolci</b> àèìòù"));
    }

    private void creaCategoriaViaApi(String nome) {
        ctx.categoriaId = ApiClient.creaCategoria(nome, "Creata dai test Selenium");
        ctx.nomeCategoria = nome;
    }

    // ---------------------------------------------------------------
    // Navigazione diretta
    // ---------------------------------------------------------------

    @Quando("apro la categoria con id {int}")
    public void aproLaCategoriaConId(int id) {
        categorie.apriCategoriaPerId(id);
    }

    // ---------------------------------------------------------------
    // Lista e ricerca
    // ---------------------------------------------------------------

    @Quando("cerco la categoria {string}")
    public void cercoLaCategoria(String testo) {
        categorie.cerca(testo);
    }

    @E("cerco la categoria creata")
    public void cercoLaCategoriaCreata() {
        categorie.cerca(ctx.nomeCategoria);
    }

    @E("azzero la ricerca")
    public void azzeroLaRicerca() {
        categorie.azzeraRicerca();
    }

    // DataTable a una colonna: Cucumber la trasforma da solo in List<String>
    @Allora("vedo in lista le categorie:")
    public void vedoInListaLeCategorie(List<String> nomi) {
        for (String nome : nomi) {
            assertTrue(categorie.isInLista(nome),
                    "La categoria '" + nome + "' doveva essere in lista");
        }
    }

    @Allora("vedo la categoria {string} in lista")
    public void vedoLaCategoriaInLista(String nome) {
        assertTrue(categorie.isInLista(nome),
                "La categoria '" + nome + "' doveva essere in lista");
    }

    @E("non vedo la categoria {string} in lista")
    public void nonVedoLaCategoriaInLista(String nome) {
        assertTrue(categorie.isNonInLista(nome),
                "La categoria '" + nome + "' NON doveva essere in lista");
    }

    @Allora("vedo la categoria creata in lista")
    public void vedoLaCategoriaCreataInLista() {
        vedoLaCategoriaInLista(ctx.nomeCategoria);
    }

    @Allora("non vedo la categoria creata in lista")
    public void nonVedoLaCategoriaCreataInLista() {
        nonVedoLaCategoriaInLista(ctx.nomeCategoria);
    }

    @E("la descrizione in lista è {string}")
    public void laDescrizioneInListaE(String attesa) {
        assertEquals(attesa, categorie.descrizioneInLista(ctx.nomeCategoria));
    }

    @Allora("il nome in lista è identico a quello salvato")
    public void ilNomeInListaEIdentico() {
        assertEquals(ctx.nomeCategoria, categorie.nomeInLista(ctx.nomeCategoria),
                "Il nome doveva essere mostrato esattamente come è stato salvato");
    }

    @E("nella cella del nome non ci sono tag {string}")
    public void nellaCellaDelNomeNonCiSonoTag(String tag) {
        assertEquals(0, categorie.contaTagNelNome(ctx.nomeCategoria, tag),
                "Il tag <" + tag + "> doveva essere mostrato come testo, non interpretato come HTML");
    }

    // ---------------------------------------------------------------
    // Form
    // ---------------------------------------------------------------

    @Quando("apro il form nuova categoria")
    public void aproIlFormNuovaCategoria() {
        categorie.apriFormNuova();
    }

    @E("apro il form di modifica della categoria creata")
    public void aproIlFormDiModificaDellaCategoriaCreata() {
        categorie.apriFormModifica(ctx.nomeCategoria);
    }

    @E("compilo il form con nome {string} e descrizione {string}")
    public void compiloIlForm(String nome, String descrizione) {
        // Per sicurezza: se per un bug il salvataggio passasse, l'@After cancella la categoria.
        // Il confronto per nome è esatto, quindi "colazione" non tocca mai "Colazione".
        if (!nome.isBlank()) {
            ctx.nomeCategoriaCreataDaUi = nome.strip();
        }
        categorie.compilaForm(nome, descrizione);
    }

    @E("svuoto il campo nome")
    public void svuotoIlCampoNome() {
        categorie.svuotaNome();
    }

    @E("clicco Salva")
    public void cliccoSalva() {
        categorie.cliccaSalva();
    }

    @Allora("il titolo della pagina è {string}")
    public void ilTitoloDellaPaginaE(String titoloAtteso) {
        assertEquals(titoloAtteso, categorie.titolo());
    }

    @E("il form contiene la descrizione {string}")
    public void ilFormContieneLaDescrizione(String attesa) {
        assertEquals(attesa, categorie.valoreDescrizioneNelForm());
    }

    @Allora("il bottone Salva è disabilitato")
    public void ilBottoneSalvaEDisabilitato() {
        assertFalse(categorie.isSalvaAbilitato(), "Il bottone Salva doveva essere disabilitato");
    }

    @Allora("il bottone Salva è abilitato")
    public void ilBottoneSalvaEAbilitato() {
        assertTrue(categorie.isSalvaAbilitato(), "Il bottone Salva doveva essere abilitato");
    }

    @Allora("vedo l'errore nel form {string}")
    public void vedoLErroreNelForm(String atteso) {
        String errore = categorie.erroreNelForm();
        assertTrue(errore.contains(atteso),
                "Nel form doveva comparire '" + atteso + "', invece c'è: '" + errore + "'");
    }

    @E("resto sul form nuova categoria")
    public void restoSulFormNuovaCategoria() {
        assertTrue(categorie.urlCorrente().contains("/categorie/nuovo"),
                "Doveva restare sul form, invece l'URL è: " + categorie.urlCorrente());
    }

    // ---------------------------------------------------------------
    // Creazione dalla UI
    // ---------------------------------------------------------------

    @E("creo una nuova categoria dal form")
    public void creoUnaNuovaCategoriaDalForm() {
        creaDalForm(DatiTest.descrizioneCasuale());
    }

    @E("creo una nuova categoria dal form senza descrizione")
    public void creoUnaNuovaCategoriaDalFormSenzaDescrizione() {
        creaDalForm("");
    }

    @E("compilo il form di una nuova categoria e annullo")
    public void compiloIlFormDiUnaNuovaCategoriaEAnnullo() {
        preparaNomeDaUi();
        categorie.apriFormNuova();
        categorie.compilaForm(ctx.nomeCategoria, "Descrizione di prova");
        categorie.annulla();
    }

    private void creaDalForm(String descrizione) {
        preparaNomeDaUi();
        ctx.descrizione = descrizione;
        categorie.apriFormNuova();
        categorie.compilaForm(ctx.nomeCategoria, descrizione);
        categorie.salva();
    }

    // Il nome va salvato PRIMA di salvare: se il test si rompe a metà,
    // l'@After sa comunque cosa cercare e cancellare
    private void preparaNomeDaUi() {
        ctx.nomeCategoria = DatiTest.nomeUnivoco("Cat Selenium");
        ctx.nomeCategoriaCreataDaUi = ctx.nomeCategoria;
    }

    // ---------------------------------------------------------------
    // Modifica
    // ---------------------------------------------------------------

    @E("modifico la categoria con un nuovo nome e una nuova descrizione")
    public void modificoLaCategoria() {
        String nuovoNome = DatiTest.nomeUnivoco("Cat Modificata");
        String nuovaDescrizione = DatiTest.descrizioneCasuale();

        categorie.apriFormModifica(ctx.nomeCategoria);
        categorie.compilaForm(nuovoNome, nuovaDescrizione);
        categorie.salva();

        // Da qui in poi "la categoria creata" è quella con il nuovo nome
        ctx.nomeCategoria = nuovoNome;
        ctx.descrizione = nuovaDescrizione;
    }

    // ---------------------------------------------------------------
    // Eliminazione
    // ---------------------------------------------------------------

    @E("elimino la categoria confermando")
    public void eliminoLaCategoriaConfermando() {
        ctx.testoConferma = categorie.elimina(ctx.nomeCategoria, true);
    }

    @E("elimino la categoria annullando la conferma")
    public void eliminoLaCategoriaAnnullandoLaConferma() {
        ctx.testoConferma = categorie.elimina(ctx.nomeCategoria, false);
    }

    @Allora("il browser ha chiesto conferma con un messaggio")
    public void ilBrowserHaChiestoConferma() {
        assertFalse(ctx.testoConferma == null || ctx.testoConferma.isBlank(),
                "Il confirm() doveva avere un messaggio");
    }

    @Allora("vedo la notifica {string}")
    public void vedoLaNotifica(String testo) {
        assertTrue(categorie.isNotificaVisibile(testo),
                "Doveva comparire la notifica: " + testo);
    }

    // ---------------------------------------------------------------
    // Verifiche nel backend (API)
    // ---------------------------------------------------------------

    @E("nel backend esiste una sola categoria con quel nome")
    public void nelBackendEsisteUnaSolaCategoria() {
        nelBackendEsisteUnaSolaCategoria(ctx.nomeCategoria);
    }

    @E("nel backend esiste una sola categoria {string}")
    public void nelBackendEsisteUnaSolaCategoria(String nome) {
        assertEquals(1, ApiClient.contaCategorieConNome(nome),
                "Nel backend doveva esserci una sola categoria '" + nome + "'");
    }

    @E("nel backend la categoria ha il nuovo nome e la nuova descrizione")
    public void nelBackendLaCategoriaHaIlNuovoNome() {
        Response risposta = ApiClient.leggiCategoria(ctx.categoriaId);
        assertEquals(200, risposta.statusCode());
        assertEquals(ctx.nomeCategoria, risposta.jsonPath().getString("nome"));
        assertEquals(ctx.descrizione, risposta.jsonPath().getString("descrizione"));
    }

    @E("nel backend la categoria non esiste più")
    public void nelBackendLaCategoriaNonEsistePiu() {
        assertEquals(404, ApiClient.leggiCategoria(ctx.categoriaId).statusCode(),
                "La categoria doveva essere cancellata dal backend");
    }

    @E("nel backend la categoria esiste ancora")
    public void nelBackendLaCategoriaEsisteAncora() {
        assertEquals(200, ApiClient.leggiCategoria(ctx.categoriaId).statusCode(),
                "La categoria NON doveva essere cancellata dal backend");
    }

    @E("nel backend il prodotto esiste ancora")
    public void nelBackendIlProdottoEsisteAncora() {
        assertEquals(200, ApiClient.leggiProdotto(ctx.prodottoId).statusCode(),
                "Il prodotto NON doveva essere cancellato dal backend");
    }
}