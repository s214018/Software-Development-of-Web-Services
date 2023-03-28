/**
 *
 * @author Gustav Nilsson Pedersen - s174562
 * @author Muaz Ahmed
 *
 *
 **/
package facades.merchant;

import domain.CorrelationId;
import domain.merchant.*;
import messaging.Event;
import messaging.MessageQueue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static messaging.domain.EventTypeResource.*;
/**
 *
 * @author Mads Klynderud (s215955)
 *
 */
public class MerchantFacadeService {

    private final Map<CorrelationId, CompletableFuture<MerchantCreationResponse>> merchantCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<List<MerchantResponse>>> merchantReportCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<PaymentResponse>> paymentCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<MerchantDeregisterResponse>> merchantDeregistrationCorrelations = new ConcurrentHashMap<>();
    MessageQueue queue;
    public MerchantFacadeService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler(MERCHANT_REGISTER_RESPONSE, this::handleMerchantCreatedResponse);
        this.queue.addHandler(MERCHANT_REPORT_RESPONSE, this::handleMerchantReportResponse);
        this.queue.addHandler(EXECUTE_PAYMENT_RESPONSE, this::handlePaymentResponse);
        this.queue.addHandler(MERCHANT_DEREGISTER_RESPONSE, this::handleMerchantDeregisterResponse);
    }


    public MerchantCreationResponse createMerchantRequest(String bankAccountID) {
        var correlationId = CorrelationId.randomId();
        merchantCorrelations.put(correlationId, new CompletableFuture<>());
        Event event = new Event(MERCHANT_REGISTER_REQUEST, new Object[] {bankAccountID, correlationId});
        queue.publish(event);
        return merchantCorrelations.get(correlationId).join();
    }

    private void handleMerchantCreatedResponse(Event event) {
        var correlationID = event.getArgument(1, CorrelationId.class);
        MerchantCreationResponse response = event.getArgument(0, MerchantCreationResponse.class);
        merchantCorrelations.get(correlationID).complete(response);
    }

    public List<MerchantResponse> getMerchantReportRequest(UUID merchantID) {
        var correlationID = CorrelationId.randomId();
        merchantReportCorrelations.put(correlationID, new CompletableFuture<>());
        Event event = new Event(MERCHANT_REPORT_REQUEST, new Object[] {merchantID, correlationID});
        this.queue.publish(event);
        return merchantReportCorrelations.get(correlationID).join();
    }

    private void handleMerchantReportResponse(Event event) {
        var correlationID = event.getArgument(1, CorrelationId.class);
        var response = Arrays.asList(event.getArgument(0, MerchantResponse[].class));
        merchantReportCorrelations.get(correlationID).complete(response);
    }

    public PaymentResponse makePaymentRequest(PaymentRequest paymentRequest) {
        var correlationId = CorrelationId.randomId();
        paymentCorrelations.put(correlationId, new CompletableFuture<>());
        Event event = new Event(EXECUTE_PAYMENT_REQUEST, new Object[] {paymentRequest, correlationId});
        this.queue.publish(event);
        return paymentCorrelations.get(correlationId).join();
    }

    private void handlePaymentResponse(Event event) {
        var correlationId = event.getArgument(1, CorrelationId.class);
        var response = event.getArgument(0, PaymentResponse.class);
        paymentCorrelations.get(correlationId).complete(response);
    }

    public MerchantDeregisterResponse deleteMerchant(UUID merchantID) {
        var correlationID = CorrelationId.randomId();
        merchantDeregistrationCorrelations.put(correlationID, new CompletableFuture<>());
        Event event = new Event(MERCHANT_DEREGISTER_REQUEST, new Object[] {merchantID, correlationID});
        this.queue.publish(event);
        return merchantDeregistrationCorrelations.get(correlationID).join();
    }

    public void handleMerchantDeregisterResponse(Event event) {
        var correlationId = event.getArgument(1, CorrelationId.class);
        var response = event.getArgument(0, MerchantDeregisterResponse.class);
        merchantDeregistrationCorrelations.get(correlationId).complete(response);
    }
}
