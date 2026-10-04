# language: it
@prodotti
Funzionalità: Lista dei prodotti
  Come gestore del ristorante
  voglio cercare e filtrare i prodotti del menu
  per trovare subito quello che mi serve

  Contesto:
    Dato che sono loggato nel gestionale

  # ---------------------------------------------------------------
  # Lista e ricerca
  # ---------------------------------------------------------------

  @smoke
  Scenario: Dalla dashboard si apre la lista prodotti con il menu base
    Quando vado alla sezione Prodotti
    Allora vedo in lista i prodotti:
      | Cornetto vuoto   |
      | Pizza Margherita |

  @filtri
  Scenario: La ricerca per nome mostra solo i prodotti corrispondenti
    Quando vado alla sezione Prodotti
    E cerco il prodotto "capp"
    Allora vedo il prodotto "Cappuccino" in lista
    E non vedo il prodotto "Cornetto vuoto" in lista

  # ---------------------------------------------------------------
  # Filtri
  # ---------------------------------------------------------------

  @filtri
  Scenario: Il filtro per categoria mostra solo i prodotti di quella categoria
    Quando vado alla sezione Prodotti
    E filtro per categoria "Colazione"
    Allora vedo il prodotto "Cornetto vuoto" in lista
    E non vedo il prodotto "Pizza Margherita" in lista

  @filtri
  Scenario: Il filtro stato Esaurito mostra solo i prodotti esauriti
    # Nel menu base nessun prodotto è esaurito: ne creo uno via API
    Dato che esiste un prodotto esaurito creato via API
    Quando vado alla sezione Prodotti
    E filtro per stato "Esaurito"
    Allora vedo il prodotto creato in lista
    E non vedo il prodotto "Cornetto vuoto" in lista

  @filtri
  Scenario: Il filtro stato Disattivo mostra solo i prodotti disattivati
    Dato che esiste un prodotto disattivo creato via API
    Quando vado alla sezione Prodotti
    E filtro per stato "Disattivo"
    Allora vedo il prodotto creato in lista
    E non vedo il prodotto "Cornetto vuoto" in lista

  @filtri
  Scenario: Il filtro stato Attivo esclude i prodotti esauriti
    # Da codice "attivo" = attivo E non esaurito
    Dato che esiste un prodotto esaurito creato via API
    Quando vado alla sezione Prodotti
    E filtro per stato "Attivo"
    Allora vedo il prodotto "Cornetto vuoto" in lista
    E non vedo il prodotto creato in lista

  @filtri
  Scenario: Filtri combinati categoria, stato e ricerca
    Dato che esistono un prodotto attivo e uno esaurito nella stessa categoria creata via API
    Quando vado alla sezione Prodotti
    E filtro per la categoria creata
    E filtro per stato "Esaurito"
    Allora vedo il prodotto esaurito in lista
    E non vedo il prodotto attivo in lista
    E non vedo il prodotto "Cornetto vuoto" in lista
    # con i filtri attivi, cercare il prodotto attivo non trova nulla
    Quando cerco il prodotto attivo
    Allora vedo il messaggio "Nessun prodotto corrisponde ai filtri selezionati."

  @filtri
  Scenario: Azzera filtri mostra di nuovo tutti i prodotti
    Quando vado alla sezione Prodotti
    Allora il bottone Azzera filtri non c'è
    Quando filtro per categoria "Cena"
    E non vedo il prodotto "Cornetto vuoto" in lista
    E azzero i filtri
    Allora vedo in lista i prodotti:
      | Cornetto vuoto   |
      | Pizza Margherita |
    E il bottone Azzera filtri non c'è

  # ---------------------------------------------------------------
  # Come vengono mostrati i dati
  # ---------------------------------------------------------------

  Scenario: Un prodotto disattivo ed esaurito mostra il badge Disattivo
    # Priorità dei badge: se non è attivo conta "Disattivo", anche se è esaurito
    Dato che esiste un prodotto disattivo ed esaurito creato via API
    Quando vado alla sezione Prodotti
    E cerco il prodotto creato
    Allora lo stato del prodotto creato è "Disattivo"

  Scenario: Prezzi formattati in euro con due decimali
    # Il prodotto via API ha prezzo 5.5 e costo 2
    Dato che esiste un prodotto creato via API
    Quando vado alla sezione Prodotti
    E cerco il prodotto creato
    Allora nella riga del prodotto creato vedo:
      | prezzo          | €5.50 |
      | costoProduzione | €2.00 |
    E la categoria del prodotto creato in lista è quella creata

  Scenario: Un prodotto senza immagine mostra il placeholder
    Dato che esiste un prodotto senza immagine creato via API
    Quando vado alla sezione Prodotti
    E cerco il prodotto creato
    Allora il prodotto creato mostra il placeholder al posto dell'immagine

  @sicurezza
  Scenario: Un nome con HTML viene mostrato come testo
    # XSS: <b> e <img onerror> devono restare testo, non diventare HTML
    Dato che esiste un prodotto con HTML nel nome creato via API
    Quando vado alla sezione Prodotti
    E cerco il prodotto creato
    Allora il nome del prodotto in lista è identico a quello salvato
    E nella cella del nome del prodotto non ci sono tag "b"
    E nella cella del nome del prodotto non ci sono tag "img"