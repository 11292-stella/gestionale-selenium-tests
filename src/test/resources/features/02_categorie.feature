# language: it
@categorie
Funzionalità: Gestione delle categorie
  Come gestore del ristorante
  voglio consultare e modificare le categorie del menu
  per organizzare i prodotti

  Contesto:
    Dato che sono loggato nel gestionale
    E vado alla sezione Categorie

  # ---------------------------------------------------------------
  # Lista e ricerca
  # ---------------------------------------------------------------

  @smoke
  Scenario: Dalla dashboard si apre la lista con il menu base
    Allora vedo in lista le categorie:
      | Colazione |
      | Pranzo    |
      | Cena      |

  @filtri
  Scenario: La ricerca trova le categorie anche per descrizione
    # "pizze" non è nel nome di nessuna categoria, solo nella descrizione di Cena
    Quando cerco la categoria "pizze"
    Allora vedo la categoria "Cena" in lista
    E non vedo la categoria "Colazione" in lista

  @filtri
  Scenario: Azzera ricerca mostra di nuovo tutte le categorie
    Quando cerco la categoria "pizze"
    E non vedo la categoria "Colazione" in lista
    E azzero la ricerca
    Allora vedo in lista le categorie:
      | Colazione |
      | Pranzo    |
      | Cena      |

  # ---------------------------------------------------------------
  # Form e validazione
  # ---------------------------------------------------------------

  Scenario: Si apre il form nuova categoria
    Quando apro il form nuova categoria
    Allora il titolo della pagina è "Nuova categoria"

  @validazione
  Scenario: Con il form vuoto Salva è disabilitato
    Quando apro il form nuova categoria
    Allora il bottone Salva è disabilitato

  @validazione
  Scenario: Senza nome la categoria non si può salvare
    Quando apro il form nuova categoria
    E compilo il form con nome "Categoria di prova" e descrizione "Descrizione di prova"
    E il bottone Salva è abilitato
    E svuoto il campo nome
    Allora il bottone Salva è disabilitato

  @validazione
  Scenario: Con un nome di soli spazi la categoria non si può salvare
    # Validators.required considera valido "   ": serve un controllo in più
    Quando apro il form nuova categoria
    E compilo il form con nome "   " e descrizione "Descrizione di prova"
    Allora il bottone Salva è disabilitato