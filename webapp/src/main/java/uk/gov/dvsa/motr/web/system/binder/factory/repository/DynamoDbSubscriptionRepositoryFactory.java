package uk.gov.dvsa.motr.web.system.binder.factory.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import uk.gov.dvsa.motr.web.component.subscription.persistence.DynamoDbSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.config.Config;
import uk.gov.dvsa.motr.web.system.binder.factory.BaseFactory;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.system.SystemVariable.DB_TABLE_SUBSCRIPTION;
import static uk.gov.dvsa.motr.web.system.SystemVariable.REGION;

public class DynamoDbSubscriptionRepositoryFactory implements BaseFactory<SubscriptionRepository> {

    private final Config config;

    @Inject
    public DynamoDbSubscriptionRepositoryFactory(Config config) {
        this.config = config;
    }

    @Override
    public SubscriptionRepository provide() {

        String region = config.getValue(REGION);
        String tableName = config.getValue(DB_TABLE_SUBSCRIPTION);
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(region).build();

        return new DynamoDbSubscriptionRepository(client, tableName);
    }
}