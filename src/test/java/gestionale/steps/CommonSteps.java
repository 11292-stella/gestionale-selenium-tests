package gestionale.steps;

import gestionale.pages.DashboardPage;
import gestionale.pages.FormPage;
import gestionale.support.ContestoTest;
import io.cucumber.java.it.Allora;
import io.cucumber.java.it.Dato;
import io.cucumber.java.it.E;
import io.cucumber.java.it.Quando;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Steps che valgono per tutte le sezioni del gestionale.
// Una frase deve esistere in UNA sola classe, altrimenti Cucumber si ferma.
public class CommonSteps {

    private final DashboardPage dashboard = new DashboardPage();
    private final FormPage form = new FormPage();
    private final ContestoTest ctx = ContestoTest.get();

    // ---------------------------------------------------------------
    // Accesso e navigazione
    // ---------------------------------------------------------------

    @Dato("che sono loggato nel gestionale")
    public void cheSonoLoggatoNelGestionale() {
        dashboard.apriDaLoggato();
    }

    // Es. "vado alla sezione Categorie" -> clicca la card e controlla /categorie
    @Quando("vado alla sezione {word}")
    public void vadoAllaSezione(String sezione) {
        dashboard.vaiAllaSezione(sezione, "/" + sezione.toLowerCase());
    }

    // ---------------------------------------------------------------
    // Form (uguali per categorie, prodotti, ...)
    // ---------------------------------------------------------------

    @Allora("il titolo della pagina è {string}")
    public void ilTitoloDellaPaginaE(String titoloAtteso) {
        assertEquals(titoloAtteso, form.titolo());
    }

    @E("scrivo {string} nel campo {string}")
    public void scrivoNelCampo(String testo, String etichetta) {
        form.scriviNelCampo(etichetta, testo);
    }

    @E("clicco Salva")
    public void cliccoSalva() {
        form.cliccaSalva();
    }

    @Allora("il bottone Salva è disabilitato")
    public void ilBottoneSalvaEDisabilitato() {
        assertFalse(form.isSalvaAbilitato(), "Il bottone Salva doveva essere disabilitato");
    }

    @Allora("il bottone Salva è abilitato")
    public void ilBottoneSalvaEAbilitato() {
        assertTrue(form.isSalvaAbilitato(), "Il bottone Salva doveva essere abilitato");
    }

    @Allora("vedo l'errore nel form {string}")
    public void vedoLErroreNelForm(String atteso) {
        String errore = form.erroreNelForm();
        assertTrue(errore.contains(atteso),
                "Nel form doveva comparire '" + atteso + "', invece c'è: '" + errore + "'");
    }

    // ---------------------------------------------------------------
    // Notifiche e conferme
    // ---------------------------------------------------------------

    @Allora("vedo la notifica {string}")
    public void vedoLaNotifica(String testo) {
        assertTrue(form.isNotificaVisibile(testo), "Doveva comparire la notifica: " + testo);
    }

    @Allora("il browser ha chiesto conferma con un messaggio")
    public void ilBrowserHaChiestoConferma() {
        assertFalse(ctx.testoConferma == null || ctx.testoConferma.isBlank(),
                "Il confirm() doveva avere un messaggio");
    }
}