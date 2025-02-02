package it.pagopa.pn.cucumber.steps.recipient;


import io.cucumber.java.DataTableType;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.testclient.*;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.AddressVerification;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.CourtesyChannelType;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.LegalChannelType;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.DataTest;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.ByteArrayInputStream;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


public class RicezioneNotificheWebSteps {


    private final IPnWebRecipientClient webRecipientClient;
    private final IPnWebUserAttributesClient iPnWebUserAttributesClient;
    private final PnPaB2bUtils b2bUtils;
    private final IPnPaB2bClient b2bClient;
    private final PnExternalServiceClientImpl externalClient;
    private final SharedSteps sharedSteps;

    private final IPnWebPaClient webPaClient;
    private HttpStatusCodeException notificationError;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public RicezioneNotificheWebSteps(SharedSteps sharedSteps, IPnWebUserAttributesClient iPnWebUserAttributesClient) {
        this.sharedSteps = sharedSteps;
        this.webRecipientClient = sharedSteps.getWebRecipientClient();
        this.b2bUtils = sharedSteps.getB2bUtils();
        this.b2bClient = sharedSteps.getB2bClient();
        this.iPnWebUserAttributesClient = iPnWebUserAttributesClient;
        this.webPaClient = sharedSteps.getWebPaClient();
        this.externalClient = sharedSteps.getPnExternalServiceClient();
    }

    @Then("la notifica può essere correttamente recuperata da {string}")
    public void notificationCanBeCorrectlyReadby(String recipient) {
        sharedSteps.selectUser(recipient);
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
        });
    }

    @Then("il documento notificato può essere correttamente recuperato da {string}")
    public void theDocumentCanBeProperlyRetrievedBy(String recipient) {
        sharedSteps.selectUser(recipient);
        NotificationAttachmentDownloadMetadataResponse downloadResponse = webRecipientClient.getReceivedNotificationDocument(
                sharedSteps.getSentNotification().getIun(),
                Integer.parseInt(sharedSteps.getSentNotification().getDocuments().get(0).getDocIdx()),
                null
        );
        AtomicReference<String> Sha256 = new AtomicReference<>("");
        Assertions.assertDoesNotThrow(() -> {
            byte[] bytes = Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(downloadResponse.getUrl()));
            Sha256.set(b2bUtils.computeSha256(new ByteArrayInputStream(bytes)));
        });
        Assertions.assertEquals(Sha256.get(), downloadResponse.getSha256());
    }


    @Then("l'allegato {string} può essere correttamente recuperato da {string}")
    public void attachmentCanBeCorrectlyRetrievedBy(String attachmentName, String recipient) {
        sharedSteps.selectUser(recipient);
        NotificationAttachmentDownloadMetadataResponse downloadResponse = webRecipientClient.getReceivedNotificationAttachment(
                sharedSteps.getSentNotification().getIun(),
                attachmentName,
                null);
        AtomicReference<String> Sha256 = new AtomicReference<>("");
        Assertions.assertDoesNotThrow(() -> {
            byte[] bytes = Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(downloadResponse.getUrl()));
            Sha256.set(b2bUtils.computeSha256(new ByteArrayInputStream(bytes)));
        });
        Assertions.assertEquals(Sha256.get(), downloadResponse.getSha256());
    }

    @And("{string} tenta il recupero dell'allegato {string}")
    public void attachmentRetrievedError(String recipient, String attachmentName) {
        this.notificationError = null;
        sharedSteps.selectUser(recipient);
        try {
            webRecipientClient.getReceivedNotificationAttachment(
                    sharedSteps.getSentNotification().getIun(),
                    attachmentName,
                    null);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
    }

    @Then("(il download)(il recupero) ha prodotto un errore con status code {string}")
    public void operationProducedErrorWithStatusCode(String statusCode) {
        Assertions.assertTrue((this.notificationError != null) &&
                (this.notificationError.getStatusCode().toString().substring(0, 3).equals(statusCode)));
    }


    @And("{string} tenta il recupero della notifica")
    public void notificationCanBeCorrectlyReadBy(String recipient) {
        sharedSteps.selectUser(recipient);
        try {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
    }


    @Then("la notifica può essere correttamente recuperata con una ricerca da {string}")
    public void notificationCanBeCorrectlyReadWithResearch(String recipient, @Transpose NotificationSearchParam searchParam) {
        sharedSteps.selectUser(recipient);
        Assertions.assertTrue(searchNotification(searchParam));
    }

    @Then("la notifica può essere correttamente recuperata con una ricerca da web PA {string}")
    public void notificationCanBeCorrectlyReadWithResearchWebPA(String paType, @Transpose NotificationSearchParamWebPA searchParam) {
        sharedSteps.selectPA(paType);
        Assertions.assertTrue(searchNotificationWebPA(searchParam));
    }

    @Then("la notifica non viene recuperata con una ricerca da {string}")
    public void notificationCantBeCorrectlyReadWithResearch(String recipient, @Transpose NotificationSearchParam searchParam) {
        sharedSteps.selectUser(recipient);
        Assertions.assertFalse(searchNotification(searchParam));
    }

    @DataTableType
    public NotificationSearchParam convertNotificationSearchParam(Map<String, String> data) {
        NotificationSearchParam searchParam = new NotificationSearchParam();

        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH);
        String monthString = (((month + "").length() == 2 || month == 9) ? (month + 1) : ("0" + (month + 1))) + "";
        int day = now.get(Calendar.DAY_OF_MONTH);
        String dayString = (day + "").length() == 2 ? (day + "") : ("0" + day);
        String start = data.getOrDefault("startDate", dayString + "/" + monthString + "/" + now.get(Calendar.YEAR));
        String end = data.getOrDefault("endDate", null);

        OffsetDateTime sentAt = sharedSteps.getSentNotification().getSentAt();
        LocalDateTime localDateStart = LocalDate.parse(start, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
        OffsetDateTime startDate = OffsetDateTime.of(localDateStart, sentAt.getOffset());

        OffsetDateTime endDate;
        if (end != null) {
            LocalDateTime localDateEnd = LocalDate.parse(end, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
            endDate = OffsetDateTime.of(localDateEnd, sentAt.getOffset());
        } else {
            endDate = sentAt;
        }

        searchParam.startDate = startDate;
        searchParam.endDate = endDate;
        //searchParam.mandateId = data.getOrDefault("mandateId",null);
        //searchParam.senderId = data.getOrDefault("senderId",null);
        searchParam.subjectRegExp = data.getOrDefault("subjectRegExp", null);
        String iun = data.getOrDefault("iunMatch", null);
        searchParam.iunMatch = ((iun != null && iun.equalsIgnoreCase("ACTUAL") ? sharedSteps.getSentNotification().getIun() : iun));
        searchParam.size = Integer.parseInt(data.getOrDefault("size", "10"));
        return searchParam;
    }

    @DataTableType
    public NotificationSearchParamWebPA convertNotificationSearchParamWebPA(Map<String, String> data) {
        NotificationSearchParamWebPA searchParam = new NotificationSearchParamWebPA();

        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH);
        String monthString = (((month + "").length() == 2 || month == 9) ? (month + 1) : ("0" + (month + 1))) + "";
        int day = now.get(Calendar.DAY_OF_MONTH);
        String dayString = (day + "").length() == 2 ? (day + "") : ("0" + day);
        String start = data.getOrDefault("startDate", dayString + "/" + monthString + "/" + now.get(Calendar.YEAR));
        String end = data.getOrDefault("endDate", null);

        OffsetDateTime sentAt = sharedSteps.getSentNotification().getSentAt();
        LocalDateTime localDateStart = LocalDate.parse(start, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
        OffsetDateTime startDate = OffsetDateTime.of(localDateStart, sentAt.getOffset());

        OffsetDateTime endDate;
        if (end != null) {
            LocalDateTime localDateEnd = LocalDate.parse(end, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
            endDate = OffsetDateTime.of(localDateEnd, sentAt.getOffset());
        } else {
            endDate = sentAt;
        }

        searchParam.startDate = startDate;
        searchParam.endDate = endDate;
        //searchParam.mandateId = data.getOrDefault("mandateId",null);
        //searchParam.senderId = data.getOrDefault("senderId",null);
        searchParam.subjectRegExp = data.getOrDefault("subjectRegExp", null);
        String iun = data.getOrDefault("iunMatch", null);
        searchParam.iunMatch = ((iun != null && iun.equalsIgnoreCase("ACTUAL") ? sharedSteps.getSentNotification().getIun() : iun));
        searchParam.size = Integer.parseInt(data.getOrDefault("size", "10"));
        return searchParam;
    }

    private boolean searchNotification(NotificationSearchParam searchParam) {
        boolean beenFound;
        NotificationSearchResponse notificationSearchResponse = webRecipientClient
                .searchReceivedNotification(
                        searchParam.startDate, searchParam.endDate, searchParam.mandateId,
                        searchParam.senderId, searchParam.status, searchParam.subjectRegExp,
                        searchParam.iunMatch, searchParam.size, null);
        List<NotificationSearchRow> resultsPage = notificationSearchResponse.getResultsPage();
        beenFound = resultsPage.stream().filter(elem -> elem.getIun().equals(sharedSteps.getSentNotification().getIun())).findAny().orElse(null) != null;
        if (!beenFound && Boolean.TRUE.equals(notificationSearchResponse.getMoreResult())) {
            while (Boolean.TRUE.equals(notificationSearchResponse.getMoreResult())) {
                List<String> nextPagesKey = notificationSearchResponse.getNextPagesKey();
                for (String pageKey : nextPagesKey) {
                    notificationSearchResponse = webRecipientClient
                            .searchReceivedNotification(
                                    searchParam.startDate, searchParam.endDate, searchParam.mandateId,
                                    searchParam.senderId, searchParam.status, searchParam.subjectRegExp,
                                    searchParam.iunMatch, searchParam.size, pageKey);
                    beenFound = resultsPage.stream().filter(elem -> elem.getIun().equals(sharedSteps.getSentNotification().getIun())).findAny().orElse(null) != null;
                    if (beenFound) break;
                }//for
                if (beenFound) break;
            }//while
        }//search cycle
        return beenFound;
    }

    private boolean searchNotificationWebPA(NotificationSearchParamWebPA searchParam) {
        boolean beenFound;
        it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationSearchResponse notificationSearchResponse = webPaClient
                .searchSentNotification(
                        searchParam.startDate, searchParam.endDate, searchParam.mandateId,
                        searchParam.status, searchParam.subjectRegExp,
                        searchParam.iunMatch, searchParam.size, null);
        List<it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationSearchRow> resultsPage = notificationSearchResponse.getResultsPage();
        beenFound = resultsPage.stream().filter(elem -> elem.getIun().equals(sharedSteps.getSentNotification().getIun())).findAny().orElse(null) != null;
        if (!beenFound && Boolean.TRUE.equals(notificationSearchResponse.getMoreResult())) {
            while (Boolean.TRUE.equals(notificationSearchResponse.getMoreResult())) {
                List<String> nextPagesKey = notificationSearchResponse.getNextPagesKey();
                for (String pageKey : nextPagesKey) {
                    notificationSearchResponse = webPaClient
                            .searchSentNotification(
                                    searchParam.startDate, searchParam.endDate, searchParam.mandateId,
                                    searchParam.status, searchParam.subjectRegExp,
                                    searchParam.iunMatch, searchParam.size, pageKey);
                    beenFound = resultsPage.stream().filter(elem -> elem.getIun().equals(sharedSteps.getSentNotification().getIun())).findAny().orElse(null) != null;
                    if (beenFound) break;
                }//for
                if (beenFound) break;
            }//while
        }//search cycle
        return beenFound;
    }

    @When("si predispone addressbook per l'utente {string}")
    public void siPredisponeAddressbook(String user) {
        switch (user) {
            case "Mario Cucumber":
                this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
                break;
            case "Mario Gherkin":
                this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
                break;
            case "Galileo Galilei":
                this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_4);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @And("viene inserito un recapito legale {string}")
    public void nuovoRecapitoLegale(String pec) {
        // inserimento
        this.iPnWebUserAttributesClient.postRecipientLegalAddress("default", LegalChannelType.PEC, (new AddressVerification().value(pec)));
        // validazione
        String verificationCode = this.externalClient.getVerificationCode(pec);
        this.iPnWebUserAttributesClient.postRecipientLegalAddress("default", LegalChannelType.PEC, (new AddressVerification().value(pec).verificationCode(verificationCode)));
    }

    @When("viene richiesto l'inserimento della pec {string}")
    public void perLUtenteVieneSettatoLaPec(String pec) {
        try {
            this.iPnWebUserAttributesClient.postRecipientLegalAddress("default", LegalChannelType.PEC, (new AddressVerification().value(pec).verificationCode("00000")));
        } catch (HttpStatusCodeException httpStatusCodeException) {
            sharedSteps.setNotificationError(httpStatusCodeException);
        }
    }

    @When("viene richiesto l'inserimento del numero di telefono {string}")
    public void vieneRichiestoLInserimentoDelNumeroDiTelefono(String phone) {
        try {
            this.iPnWebUserAttributesClient.postRecipientCourtesyAddress("default", CourtesyChannelType.SMS, (new AddressVerification().value(phone).verificationCode("00000")));
        } catch (HttpStatusCodeException httpStatusCodeException) {
            sharedSteps.setNotificationError(httpStatusCodeException);
        }
    }

    @Then("l'inserimento ha prodotto un errore con status code {string}")
    public void lInserimentoHaProdottoUnErroreConStatusCode(String statusCode) {
        HttpStatusCodeException httpStatusCodeException = this.sharedSteps.consumeNotificationError();
        Assertions.assertTrue((httpStatusCodeException != null) &&
                (httpStatusCodeException.getStatusCode().toString().substring(0, 3).equals(statusCode)));
    }

    @And("verifico che l'atto opponibile a terzi di {string} sia lo stesso")
    public void verificoAttoOpponibileSiaUguale(String timelineEventCategory, @Transpose DataTest dataFromTest) {
         it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElement timelineElement =
                 sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        // get new timeline
        String iun = sharedSteps.getSentNotification().getIun();
        sharedSteps.setSentNotification(b2bClient.getSentNotification(iun));
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElement newTimelineElement =
                sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        // check legal fact key
        Assertions.assertEquals(timelineElement.getLegalFactsIds().size(), newTimelineElement.getLegalFactsIds().size());
        for (int i = 0; i < newTimelineElement.getLegalFactsIds().size(); i++) {
            Assertions.assertEquals(newTimelineElement.getLegalFactsIds().get(i).getKey(), timelineElement.getLegalFactsIds().get(i).getKey());
        }
    }


    private static class NotificationSearchParam {
        OffsetDateTime startDate;
        OffsetDateTime endDate;
        String mandateId;
        String senderId;
        NotificationStatus status;
        String subjectRegExp;
        String iunMatch;
        Integer size = 10;
    }

    private static class NotificationSearchParamWebPA {
        OffsetDateTime startDate;
        OffsetDateTime endDate;
        String mandateId;
        it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationStatus status;
        String subjectRegExp;
        String iunMatch;
        Integer size = 10;
    }
}
