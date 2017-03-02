package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.viewmodel.UnsubscribeViewModel;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UnsubscribeResourceTest {

    private static final TemplateEngineStub TEMPLATE_ENGINE_STUB = new TemplateEngineStub();
    private static final String subscriptionId = "123-test-id";

    private UnsubscribeResource resource;
    private SubscriptionRepository subscriptionRepository;

    @Before
    public void setUp() {

        this.subscriptionRepository = mock(SubscriptionRepository.class);
        this.resource = new UnsubscribeResource(subscriptionRepository, TEMPLATE_ENGINE_STUB);
    }

    @Test(expected = NotFoundException.class)
    public void unsubscribeGetNotFoundExceptionThrownIfVehicleIsNotFound() throws Exception {

        when(subscriptionRepository.findById(any())).thenReturn(Optional.empty());

        resource.unsubscribeGet(subscriptionId);

        verify(subscriptionRepository, times(1)).findById(subscriptionId);
    }

    @Test
    public void unsubscribeGetWhenFoundDisplayPage() throws Exception {

        when(subscriptionRepository.findById(any())).thenReturn(Optional.of(subscriptionStub()));
        resource.unsubscribeGet(subscriptionId);

        verify(subscriptionRepository, times(1)).findById(subscriptionId);
        assertEquals(UnsubscribeViewModel.class, TEMPLATE_ENGINE_STUB.getContext(Map.class).get("viewModel").getClass());
        UnsubscribeViewModel viewModel = (UnsubscribeViewModel) TEMPLATE_ENGINE_STUB.getContext(Map.class).get("viewModel");
        assertEquals("test@this-is-a-test-123", viewModel.getEmail());
        assertEquals("10 July 2015", viewModel.getExpiryDate());
        assertEquals("TEST-VRM", viewModel.getRegistration());
    }

    @Test(expected = NotFoundException.class)
    public void unsubscribePostNotFoundExceptionThrownIfVehicleIsNotFound() throws Exception {

        when(subscriptionRepository.findById(any())).thenReturn(Optional.empty());

        Response actual = resource.unsubscribePost(subscriptionId);

        verify(subscriptionRepository, times(1)).findById(subscriptionId);
        assertEquals(404, actual.getStatus());
    }

    @Test
    public void unsubscribePostRedirectsToUnSubscribeConfirmationOnSuccess() throws Exception {

        Subscription subscription = subscriptionStub();
        when(subscriptionRepository.findById(any())).thenReturn(Optional.of(subscription));

        Response actual = resource.unsubscribePost(subscriptionId);

        verify(subscriptionRepository, times(1)).findById(subscriptionId);
        verify(subscriptionRepository, times(1)).delete(subscription);
        assertEquals(302, actual.getStatus());
        assertEquals(
                "unsubscribe/confirmed?expiryDate=2015-07-10&vrm=TEST-VRM&email=test%40this-is-a-test-123",
                actual.getHeaderString("Location")
        );
    }

    @Test
    public void unsubscribeConfirmedDisplaysPage() throws Exception {

        when(subscriptionRepository.findById(any())).thenReturn(Optional.of(subscriptionStub()));
        resource.unsubscribeConfrimed(LocalDate.of(2015, 7, 10).toString(), "TEST-VRM", "test@this-is-a-test-123");

        assertEquals(UnsubscribeViewModel.class, TEMPLATE_ENGINE_STUB.getContext(Map.class).get("viewModel").getClass());
        UnsubscribeViewModel viewModel = (UnsubscribeViewModel) TEMPLATE_ENGINE_STUB.getContext(Map.class).get("viewModel");
        assertEquals("test@this-is-a-test-123", viewModel.getEmail());
        assertEquals("10 July 2015", viewModel.getExpiryDate());
        assertEquals("TEST-VRM", viewModel.getRegistration());
    }

    private Subscription subscriptionStub() {
        return new Subscription(subscriptionId)
                .setEmail("test@this-is-a-test-123")
                .setMotDueDate(LocalDate.of(2015, 7, 10))
                .setVrm("TEST-VRM");
    }
}