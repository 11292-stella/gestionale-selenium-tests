package gestionale.support;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.openqa.selenium.json.Json;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ApiClient {

    // Stessa foto di default del progetto Robot
    public static final String IMMAGINE_PREDEFINITA =
            "https://images.unsplash.com/photo-1534778101976-62847782c213?w=500";

    // Trasforma le Map in JSON (è già dentro Selenium, non serve Jackson)
    private static final Json JSON = new Json();

    // Il token si chiede una volta sola e si riusa per tutti i test
    private static String token;

    private ApiClient() {
    }

    // ---------------------------------------------------------------
    // Richieste di base
    // ---------------------------------------------------------------

    private static RequestSpecification richiesta() {
        return RestAssured.given()
                .baseUri(Config.API_URL)
                .contentType(ContentType.JSON);
    }

    // Come "Headers Autenticati" in Robot: aggiunge il Bearer token
    private static RequestSpecification richiestaAutenticata() {
        return richiesta().header("Authorization", "Bearer " + ottieniToken());
    }

    // ---------------------------------------------------------------
    // Utente e autenticazione
    // ---------------------------------------------------------------

    // Come "Prepara Utente Di Test": 200 = creato, 400 = esiste già. Vanno bene entrambi.
    public static void registraUtenteDiTest() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("username", Config.USERNAME);
        body.put("password", Config.PASSWORD);
        body.put("nome", "Mario");
        body.put("cognome", "Rossi");
        body.put("email", "mrossi@test.it");
        body.put("role", 1);

        int status;
        try {
            status = richiesta().body(JSON.toJson(body))
                    .post("/api/Auth/register")
                    .statusCode();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Backend non raggiungibile su " + Config.API_URL + ": hai fatto docker compose up -d?", e);
        }

        if (status != 200 && status != 400) {
            throw new IllegalStateException("Registrazione utente di test fallita, status " + status);
        }
    }

    // Come "Ottieni Token": login via API, restituisce il JWT
    public static String ottieniToken() {
        if (token == null) {
            Map<String, Object> body = Map.of(
                    "username", Config.USERNAME,
                    "password", Config.PASSWORD);

            token = richiesta().body(JSON.toJson(body))
                    .post("/api/Auth/login")
                    .then().statusCode(200)
                    .extract().jsonPath().getString("token");
        }
        return token;
    }

    // ---------------------------------------------------------------
    // Categorie
    // ---------------------------------------------------------------

    // POST /api/Categoria -> 201. Restituisce l'id.
    public static int creaCategoria(String nome, String descrizione) {
        Map<String, Object> body = Map.of(
                "nome", nome,
                "descrizione", descrizione);

        return richiestaAutenticata().body(JSON.toJson(body))
                .post("/api/Categoria")
                .then().statusCode(201)
                .extract().jsonPath().getInt("id");
    }

    // La risposta intera: il test controlla status e campi (200 o 404)
    public static Response leggiCategoria(int id) {
        return richiestaAutenticata().get("/api/Categoria/" + id);
    }

    // Quante categorie hanno quel nome (senza distinguere maiuscole/minuscole e spazi)
    public static int contaCategorieConNome(String nome) {
        List<String> nomi = richiestaAutenticata().get("/api/Categoria")
                .then().statusCode(200)
                .extract().jsonPath().getList("nome", String.class);

        return (int) nomi.stream()
                .filter(n -> n.strip().equalsIgnoreCase(nome.strip()))
                .count();
    }

    // Per le categorie create dalla UI (id sconosciuto). Se non la trova non fa nulla.
    public static void eliminaCategoriaPerNome(String nome) {
        eliminaPerNome("/api/Categoria", nome);
    }

    // ---------------------------------------------------------------
    // Prodotti
    // ---------------------------------------------------------------

    // Prodotto "standard": attivo, non esaurito, 5.50 €, costo 2.00 €, con foto
    public static int creaProdotto(String nome, int categoriaId) {
        return creaProdotto(nome, categoriaId, true, false, 5.5, 2.0, IMMAGINE_PREDEFINITA);
    }

    // POST /api/Prodotto -> 201. Restituisce l'id. immagineUrl null = senza foto.
    public static int creaProdotto(String nome, int categoriaId, boolean attivo, boolean esaurito,
                                   double prezzo, double costo, String immagineUrl) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("nome", nome);
        body.put("descrizione", "Creato dai test Selenium");
        body.put("prezzo", prezzo);
        body.put("costoProduzione", costo);
        body.put("attivo", attivo);
        body.put("esaurito", esaurito);
        body.put("categoriaId", categoriaId);
        body.put("immagineUrl", immagineUrl);

        return richiestaAutenticata().body(JSON.toJson(body))
                .post("/api/Prodotto")
                .then().statusCode(201)
                .extract().jsonPath().getInt("id");
    }

    public static Response leggiProdotto(int id) {
        return richiestaAutenticata().get("/api/Prodotto/" + id);
    }

    // Quanti prodotti hanno ESATTAMENTE quel nome
    public static int contaProdottiConNome(String nome) {
        List<String> nomi = richiestaAutenticata().get("/api/Prodotto")
                .then().statusCode(200)
                .extract().jsonPath().getList("nome", String.class);

        return (int) nomi.stream().filter(nome::equals).count();
    }

    // Per i prodotti creati dalla UI (id sconosciuto). Se non lo trova non fa nulla.
    public static void eliminaProdottoPerNome(String nome) {
        eliminaPerNome("/api/Prodotto", nome);
    }

    // ---------------------------------------------------------------
    // Generico
    // ---------------------------------------------------------------

    // DELETE generico: 204 se ok, 404 se già cancellato. Non controlla lo status, come in Robot.
    public static void elimina(String percorso) {
        richiestaAutenticata().delete(percorso);
    }

    // Legge tutta la lista e cancella gli elementi con quel nome esatto
    private static void eliminaPerNome(String percorso, String nome) {
        List<Map<String, Object>> elementi = richiestaAutenticata().get(percorso)
                .then().statusCode(200)
                .extract().jsonPath().getList("$");

        for (Map<String, Object> e : elementi) {
            if (nome.equals(e.get("nome"))) {
                elimina(percorso + "/" + e.get("id"));
            }
        }
    }
}