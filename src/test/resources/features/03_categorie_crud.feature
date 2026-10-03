# language: it
@categorie @crud
Funzionalità: Creazione, modifica ed eliminazione delle categorie
  Come gestore del ristorante
  voglio creare, modificare ed eliminare le categorie
  senza perdere i prodotti che contengono

  Contesto:
    Dato che sono loggato nel gestionale

  # ---------------------------------------------------------------
  # Creazione
  # ---------------------------------------------------------------

  @smoke
  Scenario: Una categoria creata dal form compare in lista e nel backend
    Quando vado alla sezione Categorie
    E creo una nuova categoria dal form
    E cerco la categoria creata
    Allora vedo la categoria creata in lista
    E nel backend esiste una sola categoria con quel nome

  Scenario: Una categoria senza descrizione si salva e mostra il trattino
    Quando vado alla sezione Categorie
    E creo una nuova categoria dal form senza descrizione
    E cerco la categoria creata
    Allora vedo la categoria creata in lista
    E la descrizione in lista è "—"

  Scenario: Annulla dal form non crea la categoria
    Quando vado alla sezione Categorie
    E compilo il form di una nuova categoria e annullo
    E cerco la categoria creata
    Allora non vedo la categoria creata in lista

  @validazione
  Scenario: Non si può creare una categoria con un nome già esistente
    # " colazione " (minuscolo, con spazi) esiste già come "Colazione":
    # il backend risponde 409 e il form mostra il messaggio, senza creare un doppione
    Quando vado alla sezione Categorie
    E apro il form nuova categoria
    E compilo il form con nome " colazione " e descrizione "Doppione"
    E clicco Salva
    Allora vedo l'errore nel form "Esiste gia' una categoria"
    E resto sul form nuova categoria
    E nel backend esiste una sola categoria "Colazione"

  # ---------------------------------------------------------------
  # Modifica
  # ---------------------------------------------------------------

  Scenario: La modifica di una categoria aggiorna lista e backend
    Dato che esiste una categoria creata via API
    Quando vado alla sezione Categorie
    E cerco la categoria creata
    E modifico la categoria con un nuovo nome e una nuova descrizione
    E cerco la categoria creata
    Allora vedo la categoria creata in lista
    E nel backend la categoria ha il nuovo nome e la nuova descrizione

  Scenario: Il form di modifica contiene i dati della categoria
    Dato che esiste una categoria creata via API
    Quando vado alla sezione Categorie
    E cerco la categoria creata
    E apro il form di modifica della categoria creata
    Allora il titolo della pagina è "Modifica categoria"
    E il form contiene la descrizione "Creata dai test Selenium"

  Scenario: Una categoria inesistente mostra "Categoria non trovata"
    Quando apro la categoria con id 999999
    Allora vedo l'errore nel form "Categoria non trovata."

  # ---------------------------------------------------------------
  # Eliminazione
  # ---------------------------------------------------------------

  Scenario: Eliminare una categoria vuota la rimuove da lista e backend
    Dato che esiste una categoria creata via API
    Quando vado alla sezione Categorie
    E cerco la categoria creata
    E elimino la categoria confermando
    Allora il browser ha chiesto conferma con un messaggio
    E non vedo la categoria creata in lista
    E nel backend la categoria non esiste più

  Scenario: Annullando la conferma la categoria non viene eliminata
    Dato che esiste una categoria creata via API
    Quando vado alla sezione Categorie
    E cerco la categoria creata
    E elimino la categoria annullando la conferma
    Allora vedo la categoria creata in lista
    E nel backend la categoria esiste ancora

  @regressione
  Scenario: Una categoria con prodotti non si può eliminare
    # Eliminarla cancellerebbe a cascata menu e storico ordini:
    # il backend deve rifiutare (409) e categoria e prodotto devono restare
    Dato che esiste una categoria con un prodotto creata via API
    Quando vado alla sezione Categorie
    E cerco la categoria creata
    E elimino la categoria confermando
    Allora vedo la notifica "Impossibile eliminare la categoria"
    E vedo la categoria creata in lista
    E nel backend la categoria esiste ancora
    E nel backend il prodotto esiste ancora

  # ---------------------------------------------------------------
  # Dati particolari
  # ---------------------------------------------------------------

  @sicurezza
  Scenario: Un nome con accenti e HTML viene mostrato come testo
    # Caratteri speciali salvati identici e tag HTML mostrati come testo (niente XSS)
    Dato che esiste una categoria con accenti e HTML nel nome creata via API
    Quando vado alla sezione Categorie
    E cerco la categoria creata
    Allora il nome in lista è identico a quello salvato
    E nella cella del nome non ci sono tag "b"