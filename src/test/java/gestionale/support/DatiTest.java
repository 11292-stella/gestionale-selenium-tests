package gestionale.support;

import net.datafaker.Faker;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

// Generatori di dati di test, come test_data.resource in Robot
public final class DatiTest {

    private static final Faker faker = new Faker(Locale.ITALIAN);
    private static final Random random = new Random();

    private DatiTest() {
    }

    // I dati di un prodotto da inserire nel form.
    // "record" = classe immutabile con costruttore, getter, equals e toString già pronti.
    public record Prodotto(String nome, String descrizione, String categoria, String immagine,
                           double prezzo, double costo, boolean attivo, boolean esaurito) {

        public Prodotto conNome(String nuovoNome) {
            return new Prodotto(nuovoNome, descrizione, categoria, immagine, prezzo, costo, attivo, esaurito);
        }

        public Prodotto senzaDescrizione() {
            return new Prodotto(nome, "", categoria, immagine, prezzo, costo, attivo, esaurito);
        }
    }

    // Catalogo di piatti realistici: nome, categoria, descrizione e foto coerenti tra loro.
    // NB: niente nomi che contengono testi cercati da altri test (es. "Cappuccino", "Cornetto vuoto").
    private record Piatto(String nome, String categoria, String descrizione, String immagine) {
    }

    private static final List<Piatto> CATALOGO = List.of(
            new Piatto("Pancakes ai frutti di bosco", "Colazione",
                    "Pancakes soffici con frutti di bosco e sciroppo d'acero",
                    "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=500"),
            new Piatto("Brioche integrale al miele", "Colazione",
                    "Brioche sfogliata integrale con miele di acacia",
                    "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=500"),
            new Piatto("Avocado toast", "Colazione",
                    "Pane tostato, avocado, uovo in camicia e semi di sesamo",
                    "https://images.unsplash.com/photo-1484723091739-30a097e8f929?w=500"),
            new Piatto("Tagliatelle al ragu", "Pranzo",
                    "Tagliatelle all'uovo con ragu di manzo cotto 4 ore",
                    "https://images.unsplash.com/photo-1473093295043-cdd812d0e601?w=500"),
            new Piatto("Chicken burger", "Pranzo",
                    "Pollo croccante, insalata, pomodoro e salsa yogurt",
                    "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500"),
            new Piatto("Insalata greca", "Pranzo",
                    "Pomodori, cetrioli, feta, olive nere e cipolla rossa",
                    "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=500"),
            new Piatto("Pizza bufala e pomodorini", "Cena",
                    "Mozzarella di bufala, pomodorini freschi e basilico",
                    "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3?w=500"),
            new Piatto("Pizza tonno e cipolla", "Cena",
                    "Pomodoro, mozzarella, tonno e cipolla di Tropea",
                    "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=500"),
            new Piatto("Calzone farcito", "Cena",
                    "Calzone con prosciutto cotto, ricotta e mozzarella",
                    "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=500")
    );

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

    // Come "Genera Dati Prodotto": piatto casuale dal catalogo, prezzo e costo coerenti,
    // stato casuale (80% attivo, 20% esaurito)
    public static Prodotto generaProdotto() {
        Piatto piatto = CATALOGO.get(random.nextInt(CATALOGO.size()));
        double prezzo = arrotonda(4 + random.nextDouble() * 14);
        double costo = arrotonda(prezzo * (0.15 + random.nextDouble() * 0.25));
        boolean attivo = random.nextInt(100) < 80;
        boolean esaurito = random.nextInt(100) < 20;

        return new Prodotto(nomeUnivoco(piatto.nome()), piatto.descrizione(), piatto.categoria(),
                piatto.immagine(), prezzo, costo, attivo, esaurito);
    }

    private static double arrotonda(double valore) {
        return Math.round(valore * 100) / 100.0;
    }
}