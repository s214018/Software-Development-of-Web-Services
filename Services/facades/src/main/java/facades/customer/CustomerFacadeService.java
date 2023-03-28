/**
 *
 * @author Julia Makulec
 *
 */
package facades.customer;

import domain.CorrelationId;
import domain.customer.CustomerCreationResponse;
import domain.customer.CustomerDeregisterResponse;
import domain.customer.CustomerResponse;
import domain.customer.TokensResponse;
import messaging.Event;
import messaging.MessageQueue;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static messaging.domain.EventTypeResource.*;

public class CustomerFacadeService {
    private final Map<CorrelationId, CompletableFuture<CustomerCreationResponse>> customerCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<CustomerResponse>> customerReportCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<TokensResponse>> tokenCorrelations = new ConcurrentHashMap<>();
   private final Map<CorrelationId, CompletableFuture<CustomerDeregisterResponse>> deregistrationCorrelations = new ConcurrentHashMap<>();


    MessageQueue queue;
    public CustomerFacadeService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler(CUSTOMER_REGISTER_RESPONSE, this::handleCustomerCreatedResponse);
        this.queue.addHandler(CUSTOMER_REPORT_RESPONSE, this::handleCustomerGetReportResponse);
        this.queue.addHandler(TOKEN_GENERATION_RESPONSE, this::handleTokenGenerateResponse);
        this.queue.addHandler(CUSTOMER_DEREGISTER_RESPONSE, this::handleCustomerDeregistrationResponse);
    }

    public CustomerCreationResponse createCustomerRequest(String bankAccountID) {
        var correlationID = CorrelationId.randomId();
        customerCorrelations.put(correlationID, new CompletableFuture<>());
        Event event = new Event(CUSTOMER_REGISTER_REQUEST, new Object[] {bankAccountID, correlationID});
        queue.publish(event);
        return customerCorrelations.get(correlationID).join();
    }

    private void handleCustomerCreatedResponse(Event event) {
        var correlationID = event.getArgument(1, CorrelationId.class);
        var response = event.getArgument(0, CustomerCreationResponse.class);
        customerCorrelations.get(correlationID).complete(response);
    }

    public CustomerResponse getCustomerReport(UUID customerID) {
        var correlationID = CorrelationId.randomId();
        customerReportCorrelations.put(correlationID, new CompletableFuture<>());
        Event event = new Event(CUSTOMER_REPORT_REQUEST, new Object[] { customerID, correlationID });
        queue.publish(event);
        return customerReportCorrelations.get(correlationID).join();
    }

    private void handleCustomerGetReportResponse(Event event) {
        var correlationID = event.getArgument(1, CorrelationId.class);
        var response = event.getArgument(0, CustomerResponse.class);
        customerReportCorrelations.get(correlationID).complete(response);
    }

    public TokensResponse createTokens(UUID customerID, int numTokens) {
        var correlationID = CorrelationId.randomId();
        tokenCorrelations.put(correlationID, new CompletableFuture<>());
        Event event = new Event(TOKEN_GENERATION_REQUEST, new Object[] { customerID, numTokens, correlationID});
        queue.publish(event);
        return tokenCorrelations.get(correlationID).join();
    }

    private void handleTokenGenerateResponse(Event event) {
        var correlationID = event.getArgument(1, CorrelationId.class);
        var response =event.getArgument(0, TokensResponse.class);
        tokenCorrelations.get(correlationID).complete(response);
    }

    public CustomerDeregisterResponse deregisterCustomer(UUID customerID) {
        var correlationID = CorrelationId.randomId();
        deregistrationCorrelations.put(correlationID, new CompletableFuture<>());
        Event event = new Event(CUSTOMER_DEREGISTER_REQUEST, new Object[] {customerID, correlationID});
        queue.publish(event);
        return deregistrationCorrelations.get(correlationID).join();
    }

    public void handleCustomerDeregistrationResponse(Event event) {
        var correlationID = event.getArgument(1, CorrelationId.class);
        var response =event.getArgument(0, CustomerDeregisterResponse.class);
        deregistrationCorrelations.get(correlationID).complete(response);
    }
}
