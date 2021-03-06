package uk.gov.dvsa.motr.web.component.subscription.helper;

import uk.gov.dvsa.motr.web.helper.SystemVariableParam;

import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import static uk.gov.dvsa.motr.web.system.SystemVariable.BASE_URL;

public class UrlHelper {

    private String baseUrl;

    @Inject
    public UrlHelper(@SystemVariableParam(BASE_URL) String baseUrl) {

        this.baseUrl = baseUrl;
    }

    public String emailConfirmationPendingLink() {

        return UriBuilder.fromPath(this.baseUrl).path("email-confirmation-pending").build().toString();
    }

    public String confirmEmailLink(String confirmationId) {

        return UriBuilder.fromPath(this.baseUrl).path("confirm-email")
                .path(confirmationId).build().toString();
    }

    public String emailConfirmedFirstTimeLink() {

        return UriBuilder.fromPath(this.baseUrl).path("confirm-email/confirmed").build().toString();
    }

    public String emailConfirmedNthTimeLink() {

        return UriBuilder.fromPath(this.baseUrl).path("confirm-email/already-confirmed").build().toString();
    }

    public String unsubscribeLink(String unsubscribeId) {

        return UriBuilder.fromPath(this.baseUrl).path("unsubscribe")
                .path(unsubscribeId).build().toString();
    }
}
