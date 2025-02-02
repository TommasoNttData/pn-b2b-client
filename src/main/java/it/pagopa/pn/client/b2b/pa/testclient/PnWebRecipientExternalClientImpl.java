package it.pagopa.pn.client.b2b.pa.testclient;


import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.api.LegalFactsApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.api.RecipientReadApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnWebRecipientExternalClientImpl implements IPnWebRecipientClient {

    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;
    private final RecipientReadApi recipientReadApi;
    private final LegalFactsApi legalFactsApi;

    private BearerTokenType bearerTokenSetted = BearerTokenType.USER_2;

    private final String marioCucumberBearerToken;
    private final String marioGherkinBearerToken;
    private final String leonardoBearerToken;
    private final String dinoBearerToken;

    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;

    private final String basePath;
    private final String userAgent;

    public PnWebRecipientExternalClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.webapi.external.base-url}") String basePath,
            @Value("${pn.bearer-token.user1}") String marioCucumberBearerToken,
            @Value("${pn.bearer-token.user2}") String marioGherkinBearerToken,
            @Value("${pn.bearer-token.user3}") String leonardoBearerToken,
            @Value("${pn.bearer-token.user5}") String dinoBearerToken,
            @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
            @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken,
            @Value("${pn.webapi.external.user-agent}")String userAgent
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;

        this.marioCucumberBearerToken = marioCucumberBearerToken;
        this.marioGherkinBearerToken = marioGherkinBearerToken;
        this.leonardoBearerToken = leonardoBearerToken;
        this.dinoBearerToken = dinoBearerToken;

        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;

        this.basePath = basePath;
        this.userAgent = userAgent;
        this.recipientReadApi = new RecipientReadApi( newApiClient(restTemplate, basePath, marioGherkinBearerToken,userAgent) );
        this.legalFactsApi = new LegalFactsApi( newApiClient(restTemplate, basePath, marioGherkinBearerToken,userAgent) );
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("user-agent",userAgent);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        boolean beenSet = false;
        switch (bearerToken){
            case USER_1:
                this.recipientReadApi.setApiClient(newApiClient( restTemplate, basePath, marioCucumberBearerToken,userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_1;
                beenSet = true;
                break;
            case USER_2:
                this.recipientReadApi.setApiClient(newApiClient( restTemplate, basePath, marioGherkinBearerToken,userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_2;
                beenSet = true;
                break;
            case USER_3:
                this.recipientReadApi.setApiClient(newApiClient( restTemplate, basePath, leonardoBearerToken,userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_3;
                beenSet = true;
                break;
            case USER_5:
                this.recipientReadApi.setApiClient(newApiClient( restTemplate, basePath, dinoBearerToken,userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_5;
                beenSet = true;
                break;
            case PG_1:
                this.recipientReadApi.setApiClient(newApiClient( restTemplate, basePath, gherkinSrlBearerToken,userAgent));
                this.bearerTokenSetted = BearerTokenType.PG_1;
                beenSet = true;
                break;
            case PG_2:
                this.recipientReadApi.setApiClient(newApiClient( restTemplate, basePath, cucumberSpaBearerToken,userAgent));
                this.bearerTokenSetted = BearerTokenType.PG_2;
                beenSet = true;
                break;
        }
        return beenSet;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }



    public FullReceivedNotification getReceivedNotification(String iun, String mandateId) throws RestClientException {
        return recipientReadApi.getReceivedNotification(iun, mandateId);
    }

    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId) throws RestClientException {
        return recipientReadApi.getReceivedNotificationAttachment(iun, attachmentName, mandateId);
    }


    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument(String iun, Integer docIdx, UUID mandateId) throws RestClientException {
        return recipientReadApi.getReceivedNotificationDocument(iun, docIdx, mandateId);
    }


    public NotificationSearchResponse searchReceivedNotification(OffsetDateTime startDate, OffsetDateTime endDate, String mandateId, String senderId, NotificationStatus status, String subjectRegExp, String iunMatch, Integer size, String nextPagesKey) throws RestClientException {
        return recipientReadApi.searchReceivedNotification(startDate, endDate, mandateId, senderId, status, subjectRegExp, iunMatch, size, nextPagesKey);
    }

    public NotificationSearchResponse searchReceivedDelegatedNotification(OffsetDateTime startDate, OffsetDateTime endDate, String recipientId,String group, String senderId, NotificationStatus status, String iunMatch, Integer size, String nextPagesKey) throws RestClientException {
        return recipientReadApi.searchReceivedDelegatedNotification(startDate, endDate, senderId, recipientId, group, iunMatch, status, size, nextPagesKey);
    }

    @Override
    public LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) throws RestClientException {
        return this.legalFactsApi.getLegalFact(iun,legalFactType,legalFactId);
    }


}
