package gestionale.support;

// Memoria dello scenario: cosa è stato creato, così l'@After lo cancella.
// Un contesto per thread, come il DriverManager.
public final class ContestoTest {

    private static final ThreadLocal<ContestoTest> contesto = ThreadLocal.withInitial(ContestoTest::new);

    // Dati creati via API (id noto)
    public Integer categoriaId;
    public Integer prodottoId;

    // Dati creati dalla UI (id sconosciuto: si cancellano per nome)
    public String nomeCategoriaCreataDaUi;

    // Nomi generati nello scenario, da usare negli step successivi
    public String nomeCategoria;
    public String nuovoNomeCategoria;
    public String nomeProdotto;
    public String descrizione;

    // Testo del confirm() del browser, letto durante l'eliminazione
    public String testoConferma;

    private ContestoTest() {
    }

    public static ContestoTest get() {
        return contesto.get();
    }

    // Cancella SOLO quello che lo scenario ha creato.
    // Prima il prodotto, poi la categoria: non ci affidiamo al cascade.
    public static void pulisciDatiCreati() {
        ContestoTest c = get();
        try {
            if (c.prodottoId != null) {
                ApiClient.elimina("/api/Prodotto/" + c.prodottoId);
            }
            if (c.categoriaId != null) {
                ApiClient.elimina("/api/Categoria/" + c.categoriaId);
            }
            if (c.nomeCategoriaCreataDaUi != null) {
                ApiClient.eliminaCategoriaPerNome(c.nomeCategoriaCreataDaUi);
            }
        } finally {
            // Lo scenario successivo riparte da un contesto vuoto
            contesto.remove();
        }
    }
}