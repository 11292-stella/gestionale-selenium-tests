# language: it
@prodotti
Funzionalità: Creazione, modifica ed eliminazione dei prodotti
  Come gestore del ristorante
  voglio gestire i prodotti del menu dal form
  con controlli che impediscano dati sbagliati

  Contesto:
    Dato che sono loggato nel gestionale

  # ---------------------------------------------------------------
  # Creazione
  # ---------------------------------------------------------------

  Scenario: Si apre il form nuovo prodotto
    Quando vado alla sezione Prodotti
    E apro il form nuovo prodotto
    Allora il titolo della pagina è "Nuovo prodotto"

  @smoke @crud
  Scenario: Un prodotto creato dal form compare in lista e nel backend
    Quando vado alla sezione Prodotti
    E creo un nuovo prodotto dal form
    E cerco il prodotto creato
    Allora vedo il prodotto creato in lista
    E nel backend esiste un solo prodotto con quel nome

  @crud @validazione
  Scenario: Un prodotto senza descrizione si salva
    # La descrizione è facoltativa (prima il backend rispondeva 400)
    Quando vado alla sezione Prodotti
    E creo un nuovo prodotto dal form senza descrizione
    E cerco il prodotto creato
    Allora vedo il prodotto creato in lista

  @validazione
  Scenario: Annulla dal form non crea il prodotto
    Quando vado alla sezione Prodotti
    E compilo il form di un nuovo prodotto e annullo
    E cerco il prodotto creato
    Allora non vedo il prodotto creato in lista

  @crud @validazione
  Scenario: Il doppio click su Salva crea un solo prodotto
    # Bug classico: se Salva resta attivo durante la richiesta, partono due POST
    Quando vado alla sezione Prodotti
    E faccio doppio click su Salva per un nuovo prodotto
    Allora nel backend esiste un solo prodotto con quel nome

  @crud
  Scenario: Un nome con accenti e simboli viene salvato identico
    Quando vado alla sezione Prodotti
    E creo un nuovo prodotto dal form con nome "Caffè & Cornetto àèìòù €"
    E cerco il prodotto creato
    Allora il nome del prodotto in lista è identico a quello salvato
    E nel backend esiste un solo prodotto con quel nome

  # ---------------------------------------------------------------
  # Validazione
  # ---------------------------------------------------------------

  @validazione
  Scenario: Con il form vuoto Salva è disabilitato
    Quando vado alla sezione Prodotti
    E apro il form nuovo prodotto
    Allora il bottone Salva è disabilitato

  @validazione
  Scenario: Senza nome il prodotto non si può salvare
    Quando vado alla sezione Prodotti
    E apro il form nuovo prodotto
    E compilo il form del prodotto con dati validi
    # controllo di partenza: se poi si disabilita, è proprio per il nome vuoto
    E il bottone Salva è abilitato
    E scrivo "" nel campo "Nome"
    Allora il bottone Salva è disabilitato

  @validazione
  Scenario: Con un nome di soli spazi il prodotto non si può salvare
    Quando vado alla sezione Prodotti
    E apro il form nuovo prodotto
    E compilo il form del prodotto con dati validi
    E il bottone Salva è abilitato
    E scrivo "   " nel campo "Nome"
    Allora il bottone Salva è disabilitato

  @validazione
  Scenario: Con prezzo negativo il prodotto non si può salvare
    Quando vado alla sezione Prodotti
    E apro il form nuovo prodotto
    E compilo il form del prodotto con dati validi
    E il bottone Salva è abilitato
    E scrivo "-5" nel campo "Prezzo"
    Allora il bottone Salva è disabilitato

  @validazione
  Scenario: Con prezzo zero il prodotto non si può salvare
    # Il backend richiede prezzo >= 0.01: prima il form accettava 0 e il salvataggio dava 400
    Quando vado alla sezione Prodotti
    E apro il form nuovo prodotto
    E compilo il form del prodotto con dati validi
    E il bottone Salva è abilitato
    E scrivo "0" nel campo "Prezzo"
    Allora il bottone Salva è disabilitato

  # ---------------------------------------------------------------
  # Modifica
  # ---------------------------------------------------------------

  @crud
  Scenario: La modifica di un prodotto aggiorna lista e backend
    Dato che esiste un prodotto creato via API
    Quando vado alla sezione Prodotti
    E cerco il prodotto creato
    E modifico il prodotto con nuovi dati
    E cerco il prodotto creato
    Allora vedo il prodotto creato in lista
    Quando cerco il nome precedente del prodotto
    Allora non vedo il nome precedente del prodotto in lista
    E nel backend il prodotto ha il nuovo nome e il nuovo prezzo

  @crud
  Scenario: Il form di modifica contiene tutti i dati del prodotto
    Dato che esiste un prodotto esaurito con prezzo 7.25 e costo 3.1 creato via API
    Quando vado alla sezione Prodotti
    E cerco il prodotto creato
    E apro il form di modifica del prodotto creato
    Allora il titolo della pagina è "Modifica prodotto"
    E il form del prodotto contiene:
      | Prezzo           | 7.25 |
      | Costo produzione | 3.1  |
    E nel form la categoria selezionata è quella creata
    E lo switch "Attivo" è acceso
    E lo switch "Esaurito" è acceso

  @crud
  Scenario: Cambiare categoria in modifica aggiorna la lista
    Dato che esiste un prodotto creato via API
    E esiste un'altra categoria creata via API
    Quando vado alla sezione Prodotti
    E cerco il prodotto creato
    E apro il form di modifica del prodotto creato
    E scelgo l'altra categoria nel form
    E salvo il prodotto
    E cerco il prodotto creato
    Allora la categoria del prodotto creato in lista è l'altra categoria

  @crud
  Scenario: Annulla in modifica non salva le modifiche
    Dato che esiste un prodotto creato via API
    Quando vado alla sezione Prodotti
    E cerco il prodotto creato
    E apro il form di modifica del prodotto creato
    E scrivo "Nome che non deve essere salvato" nel campo "Nome"
    E annullo il form del prodotto
    Allora nel backend il prodotto ha ancora il nome originale

  Scenario: Un prodotto inesistente mostra "Prodotto non trovato"
    Quando apro il prodotto con id 999999
    Allora vedo l'errore nel form "Prodotto non trovato."

  # ---------------------------------------------------------------
  # Eliminazione
  # ---------------------------------------------------------------

  @crud
  Scenario: Eliminare un prodotto lo rimuove da lista e backend
    Dato che esiste un prodotto creato via API
    Quando vado alla sezione Prodotti
    E cerco il prodotto creato
    E elimino il prodotto confermando
    Allora il browser ha chiesto conferma con un messaggio
    E non vedo il prodotto creato in lista
    E nel backend il prodotto non esiste più

  @crud
  Scenario: Annullando la conferma il prodotto non viene eliminato
    Dato che esiste un prodotto creato via API
    Quando vado alla sezione Prodotti
    E cerco il prodotto creato
    E elimino il prodotto annullando la conferma
    Allora vedo il prodotto creato in lista
    E nel backend il prodotto esiste ancora