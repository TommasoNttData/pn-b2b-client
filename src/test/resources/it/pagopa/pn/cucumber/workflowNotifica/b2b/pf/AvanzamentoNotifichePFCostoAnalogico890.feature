Feature: costo notifica con workflow analogico per persona fisica 890

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"

  @dev @costoAnalogico @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_PF_890_1] Invio notifica verifica costo con FSU + @OK_890 + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | REGISTERED_LETTER_890           |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      |  physicalAddress_zip    |    <CAP>   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    Then viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO |
      | 05010 | 1105  |

  @dev @costoAnalogico @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_PF_890_2] Invio notifica verifica costo con FSU + @OK_890 + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | REGISTERED_LETTER_890           |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      |  physicalAddress_zip    |    <CAP>   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "<COSTO>" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO |
      | 05010 | 0     |

  @dev @costoAnalogico @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_PF_890_3] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + DELIVERY_MODE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | REGISTERED_LETTER_890           |
      | feePolicy             | DELIVERY_MODE                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      |  physicalAddress_zip    |    <CAP>   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    Then viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO |
      | 70010 | 861   |
      | 00010 | 906   |
      | 60010 | 979   |
      | 64010 | 954   |
      | 06031 | 957   |
      | 10012 | 925   |


  @dev @costoAnalogico @costoCartAAR
  Scenario Outline: [B2B_COSTO_ANALOG_PF_890_4] Invio notifica e verifica costo con RECAPITISTA + @OK_890 + FLAT_RATE positivo
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | REGISTERED_LETTER_890           |
      | feePolicy             | FLAT_RATE                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      |  physicalAddress_zip    |    <CAP>   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato il costo = "<COSTO>" della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "<COSTO>" della notifica
    Examples:
      | CAP   | COSTO |
      | 70010 | 0     |
      | 00010 | 0     |
      | 60010 | 0     |
      | 64010 | 0     |
      | 06031 | 0     |
      | 10012 | 0     |