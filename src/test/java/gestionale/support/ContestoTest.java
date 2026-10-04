package gestionale.support;

// Memoria dello scenario: cosa è stato creato, così l'@After lo cancella.
// Un contesto per thread, come il DriverManager.
public final class ContestoTest {

    private static final ThreadLocal<ContestoTest> contesto = ThreadLocal.withInitial(ContestoTest::new);

    // Dati creati via API (id noto)
    public Integer categoriaId;
    public Integer categoriaId2;
    public Integer prodottoId;
    public Integer prodottoId2;

    // Dati creati dalla UI (id sconosciuto: si cancellano per nome)
    public String nomeCategoriaCreataDaUi;
    public String nomeProdottoCreatoDaUi;

    // Nomi e dati generati nello scenario, da usare negli step successivi
    public String nomeCategoria;
    public String nomeCategoria2;
    public String nomeProdotto;
    public String nomeProdotto2;
    public String nomeProdottoPrecedente;
    public String descrizione;
    public DatiTest.Prodotto prodotto;

    // Testo del confirm() del browser, letto durante l'eliminazione
    public String testoConferma;

    private ContestoTest() {
    }

    public static ContestoTest get() {
        return contesto.get();
    }

    // Cancella SOLO quello che lo scenario ha creato.
    // Prima i prodotti, poi le categorie: non ci affidiamo al cascade.
    public static void pulisciDatiCreati() {
        ContestoTest c = get();
        try {
            if (c.prodottoId != null) {
                ApiClient.elimina("/api/Prodotto/" + c.prodottoId);
            }
            if (c.prodottoId2 != null) {
                ApiClient.elimina("/api/Prodotto/" + c.prodottoId2);
            }
            if (c.nomeProdottoCreatoDaUi != null) {
                ApiClient.eliminaProdottoPerNome(c.nomeProdottoCreatoDaUi);
            }
            if (c.categoriaId != null) {
                ApiClient.elimina("/api/Categoria/" + c.categoriaId);
            }
            if (c.categoriaId2 != null) {
                ApiClient.elimina("/api/Categoria/" + c.categoriaId2);
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