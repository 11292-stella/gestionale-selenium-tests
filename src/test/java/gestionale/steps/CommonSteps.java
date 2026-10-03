package gestionale.steps;

import gestionale.pages.DashboardPage;
import io.cucumber.java.it.Dato;
import io.cucumber.java.it.Quando;

// Steps che valgono per tutte le sezioni del gestionale
public class CommonSteps {

    private final DashboardPage dashboard = new DashboardPage();

    @Dato("che sono loggato nel gestionale")
    public void cheSonoLoggatoNelGestionale() {
        dashboard.apriDaLoggato();
    }

    // Es. "vado alla sezione Categorie" -> clicca la card e controlla /categorie
    @Quando("vado alla sezione {word}")
    public void vadoAllaSezione(String sezione) {
        dashboard.vaiAllaSezione(sezione, "/" + sezione.toLowerCase());
    }
}