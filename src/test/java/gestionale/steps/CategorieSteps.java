package gestionale.steps;

import gestionale.pages.CategoriePage;
import io.cucumber.java.it.Allora;
import io.cucumber.java.it.E;
import io.cucumber.java.it.Quando;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CategorieSteps {

    private final CategoriePage categorie = new CategoriePage();

    // ---------------------------------------------------------------
    // Lista e ricerca
    // ---------------------------------------------------------------

    @Quando("cerco la categoria {string}")
    public void cercoLaCategoria(String testo) {
        categorie.cerca(testo);
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

    // ---------------------------------------------------------------
    // Form
    // ---------------------------------------------------------------

    @Quando("apro il form nuova categoria")
    public void aproIlFormNuovaCategoria() {
        categorie.apriFormNuova();
    }

    @E("compilo il form con nome {string} e descrizione {string}")
    public void compiloIlForm(String nome, String descrizione) {
        categorie.compilaForm(nome, descrizione);
    }

    @E("svuoto il campo nome")
    public void svuotoIlCampoNome() {
        categorie.svuotaNome();
    }

    @Allora("il titolo della pagina è {string}")
    public void ilTitoloDellaPaginaE(String titoloAtteso) {
        assertEquals(titoloAtteso, categorie.titolo());
    }

    @Allora("il bottone Salva è disabilitato")
    public void ilBottoneSalvaEDisabilitato() {
        assertFalse(categorie.isSalvaAbilitato(), "Il bottone Salva doveva essere disabilitato");
    }

    @Allora("il bottone Salva è abilitato")
    public void ilBottoneSalvaEAbilitato() {
        assertTrue(categorie.isSalvaAbilitato(), "Il bottone Salva doveva essere abilitato");
    }
}
