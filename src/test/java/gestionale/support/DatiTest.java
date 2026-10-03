package gestionale.support;

import net.datafaker.Faker;

import java.util.Locale;
import java.util.UUID;

// Generatori di dati di test, come test_data.resource in Robot
public final class DatiTest {

    private static final Faker faker = new Faker(Locale.ITALIAN);

    private DatiTest() {
    }

    // Come "Genera Nome Univoco": prefisso + 8 caratteri casuali.
    // Es. nomeUnivoco("Cat Selenium") -> "Cat Selenium 3f9a1c2b"
    public static String nomeUnivoco(String prefisso) {
        String suffisso = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return prefisso + " " + suffisso;
    }

    // Come FakerLibrary.Sentence nb_words=6
    public static String descrizioneCasuale() {
        return faker.lorem().sentence(6);
    }
}