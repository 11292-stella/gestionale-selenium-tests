package gestionale.support;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class ApiClient {

    private static final HttpClient http = HttpClient.newHttpClient();

    private ApiClient() {
    }

    // Come "Prepara Utente Di Test" in Robot:
    // 200 = creato, 400 = esiste già. Vanno bene entrambi.
    public static void registraUtenteDiTest() {
        String body = """
                {
                  "username": "%s",
                  "password": "%s",
                  "nome": "Mario",
                  "cognome": "Rossi",
                  "email": "mrossi@test.it",
                  "role": 1
                }
                """.formatted(Config.USERNAME, Config.PASSWORD);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Config.API_URL + "/api/Auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status != 200 && status != 400) {
                throw new IllegalStateException("Registrazione utente di test fallita, status " + status);
            }
        } catch (java.io.IOException | InterruptedException e) {
            throw new IllegalStateException(
                    "Backend non raggiungibile su " + Config.API_URL + ": hai fatto docker compose up -d?", e);
        }
    }
}