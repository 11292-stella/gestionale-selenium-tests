# language: it
@categorie @prodotti
Funzionalità: Collegamento tra categorie e prodotti
  Come gestore del ristorante
  voglio che le modifiche alle categorie si vedano subito nei prodotti

  Contesto:
    Dato che sono loggato nel gestionale

  @crud
  Scenario: Rinominare una categoria aggiorna anche la lista prodotti
    Dato che esiste un prodotto creato via API
    Quando vado alla sezione Categorie
    E cerco la categoria creata
    E modifico la categoria con un nuovo nome e una nuova descrizione
    E apro la lista prodotti
    E cerco il prodotto creato
    Allora la categoria del prodotto creato in lista è quella creata
    Quando filtro per la categoria creata
    Allora vedo il prodotto creato in lista

  Scenario: Una nuova categoria è subito disponibile nel form prodotto
    Dato che esiste una categoria creata via API
    Quando apro direttamente il form nuovo prodotto
    E scelgo la categoria creata nel form
    Allora nel form la categoria selezionata è quella creata