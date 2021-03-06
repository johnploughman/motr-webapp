package uk.gov.dvsa.motr.web.component.subscription.persistence;

import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;

import java.util.Optional;


public interface SubscriptionRepository {

    Optional<Subscription> findByUnsubscribeId(String id);

    Optional<Subscription> findByVrmAndEmail(String vrm, String email);

    void save(Subscription subscription);

    void delete(Subscription subscription);
}
