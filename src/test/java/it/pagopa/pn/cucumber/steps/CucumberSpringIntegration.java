package it.pagopa.pn.cucumber.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.impl.PnPaB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.springconfig.ApiKeysConfiguration;
import it.pagopa.pn.client.b2b.pa.springconfig.BearerTokenConfiguration;
import it.pagopa.pn.client.b2b.pa.springconfig.RestTemplateConfiguration;
import it.pagopa.pn.client.b2b.pa.testclient.*;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = {
        ApiKeysConfiguration.class,
        BearerTokenConfiguration.class,
        RestTemplateConfiguration.class,
        PnPaB2bUtils.class,
        PnPaB2bExternalClientImpl.class,
        PnWebRecipientExternalClientImpl.class,
        PnWebhookB2bExternalClientImpl.class,
        PnWebMandateExternalClientImpl.class,
        PnSafeStorageInfoExternalClientImpl.class,
        PnWebUserAttributesExternalClientImpl.class,
        PnAppIOB2bExternalClientImpl.class,
        PnApiKeyManagerExternalClientImpl.class
})
public class CucumberSpringIntegration {
}
