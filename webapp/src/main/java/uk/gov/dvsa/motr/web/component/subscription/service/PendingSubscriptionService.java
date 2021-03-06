package uk.gov.dvsa.motr.web.component.subscription.service;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.PendingSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.eventlog.subscription.PendingSubscriptionCreatedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.PendingSubscriptionCreationFailedEvent;

import java.time.LocalDate;
import java.util.Optional;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.component.subscription.service.RandomIdGenerator.generateId;

public class PendingSubscriptionService {

    private PendingSubscriptionRepository pendingSubscriptionRepository;
    private SubscriptionRepository subscriptionRepository;
    private NotifyService notifyService;
    private UrlHelper urlHelper;

    @Inject
    public PendingSubscriptionService(
            PendingSubscriptionRepository pendingSubscriptionRepository,
            SubscriptionRepository subscriptionRepository,
            NotifyService notifyService,
            UrlHelper urlHelper
    ) {

        this.pendingSubscriptionRepository = pendingSubscriptionRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.notifyService = notifyService;
        this.urlHelper = urlHelper;
    }

    public String handlePendingSubscriptionCreation(String vrm, String email, LocalDate motDueDate, String motTestNumber) {

        Optional<Subscription> subscription = subscriptionRepository.findByVrmAndEmail(vrm, email);

        if (subscription.isPresent()) {
            updateSubscriptionMotDueDate(subscription.get(), motDueDate);

            return urlHelper.emailConfirmedNthTimeLink();
        } else {
            createPendingSubscription(vrm, email, motDueDate, generateId(), motTestNumber);

            return urlHelper.emailConfirmationPendingLink();
        }
    }

    /**
     * Creates pending subscription in the system to be confirmed later by confirmation link
     * @param vrm        subscription vrm
     * @param email      subscription email
     * @param motDueDate most recent mot due date
     * @param motTestNumber
     */
    public void createPendingSubscription(String vrm, String email, LocalDate motDueDate, String confirmationId, String motTestNumber) {

        PendingSubscription pendingSubscription = new PendingSubscription()
                .setConfirmationId(confirmationId)
                .setEmail(email)
                .setVrm(vrm)
                .setMotDueDate(motDueDate)
                .setMotTestNumber(motTestNumber);

        try {
            pendingSubscriptionRepository.save(pendingSubscription);
            notifyService.sendEmailAddressConfirmationEmail(email, urlHelper.confirmEmailLink(pendingSubscription.getConfirmationId()));
            EventLogger.logEvent(
                    new PendingSubscriptionCreatedEvent().setVrm(vrm).setEmail(email).setMotDueDate(motDueDate)
                    .setMotTestNumber(motTestNumber)
            );
        } catch (Exception e) {
            EventLogger.logErrorEvent(
                    new PendingSubscriptionCreationFailedEvent().setVrm(vrm).setEmail(email).setMotDueDate(motDueDate)
                    .setMotTestNumber(motTestNumber), e);
            throw e;
        }
    }

    private Subscription updateSubscriptionMotDueDate(Subscription subscription, LocalDate motDueDate) {

        subscription.setMotDueDate(motDueDate);
        subscriptionRepository.save(subscription);

        return subscription;
    }
}
