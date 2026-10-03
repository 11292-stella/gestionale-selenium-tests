# language: it
@login
Funzionalità: Login al gestionale
  Come utente del ristorante
  voglio accedere al gestionale con le mie credenziali
  per poter gestire prodotti, categorie e ordini

  Contesto:
    Dato che sono sulla pagina di login

  @smoke
  Scenario: Login con credenziali valide porta alla dashboard
    Quando eseguo il login con le credenziali dell'utente di test
    Allora vedo la dashboard

  Scenario: Login con password errata mostra un messaggio di errore
    Quando eseguo il login con username "mrossi" e password "password-sbagliata"
    Allora vedo il messaggio di errore
    E resto sulla pagina di login

  Scenario: Il bottone Accedi è disabilitato con i campi vuoti
    Allora il bottone Accedi è disabilitato