package gestionale.support;

public final class Config {

    private Config() {
    }

    // URL del gestionale Angular (ng serve)
    public static final String BASE_URL =
            leggi("baseUrl", "BASE_URL", "http://localhost:4200");

    // URL del backend .NET (Docker), per login e dati via API
    public static final String API_URL =
            leggi("apiUrl", "API_BASE_URL", "http://localhost:5231");

    // Utente di test locale (lo stesso dei test Robot e Appium)
    public static final String USERNAME =
            leggi("username", "APP_USERNAME", "mrossi");

    public static final String PASSWORD =
            leggi("password", "APP_PASSWORD", "password123");

    // Quanti secondi aspettare al massimo un elemento
    public static final int TIMEOUT_SECONDI =
            Integer.parseInt(leggi("timeout", "TIMEOUT_SECONDI", "10"));

    // Ordine di priorità: -D da riga di comando > variabile d'ambiente > valore predefinito
    private static String leggi(String proprieta, String variabileAmbiente, String predefinito) {
        String valore = System.getProperty(proprieta);
        if (valore != null && !valore.isBlank()) {
            return valore;
        }

        valore = System.getenv(variabileAmbiente);
        if (valore != null && !valore.isBlank()) {
            return valore;
        }

        return predefinito;
    }
}