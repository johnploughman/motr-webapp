package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidConfirmationIdException;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionConfirmationService;
import uk.gov.dvsa.motr.web.cookie.EmailConfirmationParams;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.helper.DateDisplayHelper;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.viewmodel.EmailConfirmedViewModel;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.VRM_KEY;

import static java.util.Collections.emptyMap;

@Singleton
@Path("/confirm-email")
@Produces("text/html")
public class EmailConfirmedResource {

    private final TemplateEngine renderer;
    private final DataLayerHelper dataLayerHelper;
    private SubscriptionConfirmationService subscriptionConfirmationService;
    private MotrSession motrSession;
    private UrlHelper urlHelper;

    @Inject
    public EmailConfirmedResource(
            TemplateEngine renderer,
            SubscriptionConfirmationService pendingSubscriptionActivatorService,
            MotrSession motrSession,
            UrlHelper urlHelper
    ) {

        this.renderer = renderer;
        this.subscriptionConfirmationService = pendingSubscriptionActivatorService;
        this.motrSession = motrSession;
        this.urlHelper = urlHelper;
        this.dataLayerHelper = new DataLayerHelper();
    }

    @GET
    @Path("{confirmationId}")
    public Response confirmEmailGet(@PathParam("confirmationId") String confirmationId) {

        try {
            Subscription subscription = subscriptionConfirmationService.confirmSubscription(confirmationId);
            motrSession.clear();
            addSubscriptionDetailsToSession(subscription);
            return RedirectResponseBuilder.redirect(urlHelper.emailConfirmedFirstTimeLink());
        } catch (InvalidConfirmationIdException e) {
            return Response.ok(renderer.render("subscription-error", emptyMap())).build();
        }
    }

    @GET
    @Path("confirmed")
    public String confirmEmailFirstTimeGet() {

        return showConfirmationPage();
    }

    @GET
    @Path("already-confirmed")
    public String confirmEmailNthTimeGet() {

        return showConfirmationPage();
    }

    private void addSubscriptionDetailsToSession(Subscription subscription) {

        EmailConfirmationParams params = new EmailConfirmationParams();
        params.setRegistration(subscription.getVrm());
        params.setExpiryDate(DateDisplayHelper.asDisplayDate(subscription.getMotDueDate()));
        params.setEmail(subscription.getEmail());
        motrSession.setEmailConfirmationParams(params);
    }

    private String showConfirmationPage() {

        EmailConfirmationParams subscription = motrSession.getEmailConfirmationParams();
        if (null != subscription) {
            dataLayerHelper.putAttribute(VRM_KEY, subscription.getRegistration());
            return renderer.render("subscription-confirmation", buildViewModel(subscription));
        } else {
            return renderer.render("subscription-error", emptyMap());
        }
    }

    private Map<String, Object> buildViewModel(EmailConfirmationParams subscription) {

        Map<String, Object> map = new HashMap<>();
        EmailConfirmedViewModel viewModel = new EmailConfirmedViewModel();

        map.putAll(dataLayerHelper.formatAttributes());
        map.put("viewModel", viewModel
                .setEmail(subscription.getEmail())
                .setExpiryDate(subscription.getExpiryDate())
                .setRegistration(subscription.getRegistration()));

        return map;
    }
}
