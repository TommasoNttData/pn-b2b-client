Feature: Scelta canale di invio (Digitale o analogico)

  @e2e
  Scenario: [E2E-PF-CHOOSE-DELIVERY-MODE-1] Invio notifica mono destinatario. L’utente ha configurato l’indirizzo di piattaforma
    Given si predispone addressbook per l'utente "Mr. IndirizzoPiattaforma"
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario "Mr. IndirizzoPiattaforma"
      | NULL | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 1.5 |
      | numCheck    | 3    |
    And si aggiunge alla sequence il controllo che "GET_ADDRESS" esista
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF-CHOOSE-DELIVERY-MODE-2] Invio notifica mono destinatario. L’utente NON ha configurato l’indirizzo di piattaforma MA ha valorizzato l’indirizzo Speciale
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile_address | testpagopa1@pnpagopa.postecert.local |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 1.5 |
      | numCheck    | 3    |
    And si aggiunge alla sequence il controllo che "GET_ADDRESS" esista
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And si aggiunge alla sequence il controllo che "GET_ADDRESS" esista
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificata la sequence

  @e2e @OnlyEnvTest
  Scenario: [E2E-PF-CHOOSE-DELIVERY-MODE-3] Invio notifica mono destinatario. L’utente NON ha configurato l’indirizzo di piattaforma,
  NON ha valorizzato l’indirizzo Speciale MA ha valorizzato l’indirizzo GENERALE
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario "Mr. IndirizzoGenerale"
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 1.5 |
      | numCheck    | 3    |
    And si aggiunge alla sequence il controllo che "GET_ADDRESS" esista
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And si aggiunge alla sequence il controllo che "GET_ADDRESS" esista
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And si aggiunge alla sequence il controllo che "GET_ADDRESS" esista
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF-CHOOSE-DELIVERY-MODE-4] Invio notifica mono destinatario. L’utente non ha configurato nessuno degli indirizzi digitali
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "GET_ADDRESS" esista
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And si aggiunge alla sequence il controllo che "GET_ADDRESS" esista
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And si aggiunge alla sequence il controllo che "GET_ADDRESS" esista
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And si aggiunge alla sequence il controllo che "SCHEDULE_ANALOG_WORKFLOW" esista
      | details_recIndex | 0 |
    And viene verificata la sequence


