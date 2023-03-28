import domain.*;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import messaging.Event;
import messaging.MessageQueue;
import requests.PaymentRequest;
import responses.CustomerRetrieveResponse;
import responses.MerchantRetrieveResponse;
import responses.PaymentResponse;
import responses.TokenActionResponse;

import static messaging.domain.EventMessages.*;
import static messaging.domain.EventTypeResource.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author Gustav Nilsson Pedersen - s174562
 * **/
public class PaymentService {
    private final BankService bankService = new BankServiceService().getBankServicePort();

    private final Map<CorrelationId, CompletableFuture<TokenActionResponse>> correlations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<CustomerRetrieveResponse>> correlationsCustomer = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<MerchantRetrieveResponse>> correlationsMerchant = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<PaymentResponse>> correlationsPaymentRegistration = new ConcurrentHashMap<>();
    MessageQueue queue;
    public PaymentService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler(TOKEN_CONSUME_FINISHED, this::handleTokenConsumeFinished);
        this.queue.addHandler(TOKEN_VERIFY_FINISHED, this::handleTokenVerifyFinished);
        this.queue.addHandler(GET_CUSTOMER_FINISHED, this::handleGetCustomerFinished);
        this.queue.addHandler(GET_MERCHANT_FINISHED, this::handleGetMerchantFinished);
        this.queue.addHandler(PAYMENT_REGISTERED, this::handlePaymentFinished);
        this.queue.addHandler(EXECUTE_PAYMENT_REQUEST, this::handleExecutePaymentRequest);
    }

    public PaymentResponse makePayment(PaymentRequest paymentRequest) throws BankServiceException_Exception{
        TokenActionResponse customerVerifyResponse = verifyToken(paymentRequest.getToken());

        if (!customerVerifyResponse.getMessage().equals(TOKEN_ACTI0N_SUCCESSFUL)) return new PaymentResponse(customerVerifyResponse.getMessage());

        TokenActionResponse tokenIDConsumed = consumeToken(paymentRequest.getToken());
        if (!tokenIDConsumed.getMessage().equals(TOKEN_ACTI0N_SUCCESSFUL)) return new PaymentResponse(customerVerifyResponse.getMessage());

        CustomerRetrieveResponse customerRetrieveResponse = getCustomer(customerVerifyResponse.getUuid());
        if (!customerRetrieveResponse.getMessage().equals(ACCOUNT_RETRIEVAL_SUCCESSFUL)) return new PaymentResponse(customerRetrieveResponse.getMessage());

        MerchantRetrieveResponse merchantRetrieveResponse = getMerchant(paymentRequest.getMerchantID());
        if (!merchantRetrieveResponse.getMessage().equals(ACCOUNT_RETRIEVAL_SUCCESSFUL)) return new PaymentResponse(merchantRetrieveResponse.getMessage());

        Payment p = new Payment();
        p.setMerchantID(paymentRequest.getMerchantID());
        p.setCustomerID(customerVerifyResponse.getUuid());
        p.setAmount(paymentRequest.getAmount());
        p.setToken(paymentRequest.getToken());

        requestMoneyTransfer(
                customerRetrieveResponse.getCustomer().getBankAccountID()
                , merchantRetrieveResponse.getMerchant().getBankAccountID()
                , p.getAmount());
        return registerPayment(p);
    }

    private void handleExecutePaymentRequest(Event e) {
        var correlationID = e.getArgument(1, CorrelationId.class);
        PaymentResponse response;
        try {
            response = makePayment(e.getArgument(0, PaymentRequest.class));
        } catch (BankServiceException_Exception ex) {
            response = new PaymentResponse(ex.getMessage());
        }
        Event event = new Event(EXECUTE_PAYMENT_RESPONSE, new Object[] {response, correlationID});
        this.queue.publish(event);
    }

    private PaymentResponse registerPayment(Payment payment) {
        var correlationId = CorrelationId.randomId();
        correlationsPaymentRegistration.put(correlationId,new CompletableFuture<>());
        Event event = new Event(REQUEST_REGISTER_PAYMENT, new Object[] { payment, correlationId });
        queue.publish(event);
        return correlationsPaymentRegistration.get(correlationId).join();
    }

    private CustomerRetrieveResponse getCustomer(UUID customerID) {
        var correlationId = CorrelationId.randomId();
        correlationsCustomer.put(correlationId,new CompletableFuture<>());
        Event event = new Event(GET_CUSTOMER_REQUEST, new Object[] { customerID, correlationId });
        queue.publish(event);
        return correlationsCustomer.get(correlationId).join();
    }

    private MerchantRetrieveResponse getMerchant(UUID merchantID) {
        var correlationId = CorrelationId.randomId();
        correlationsMerchant.put(correlationId,new CompletableFuture<>());
        Event event = new Event(GET_MERCHANT_REQUEST, new Object[] { merchantID, correlationId });
        queue.publish(event);
        return correlationsMerchant.get(correlationId).join();
    }


    private void requestMoneyTransfer(String customerBankAccount, String merchantBankAccount, double amount) throws BankServiceException_Exception {
        BigDecimal amountObject = new BigDecimal(amount);
        bankService.transferMoneyFromTo(customerBankAccount, merchantBankAccount, amountObject, "test");
    }

    public TokenActionResponse verifyToken(UUID tokenID){
        var correlationId = CorrelationId.randomId();
        correlations.put(correlationId,new CompletableFuture<>());
        Event event = new Event(TOKEN_VERIFY_REQUEST, new Object[] { tokenID, correlationId });
        queue.publish(event);
        return correlations.get(correlationId).join();
    }

    public TokenActionResponse consumeToken(UUID tokenID){
        var correlationId = CorrelationId.randomId();
        correlations.put(correlationId,new CompletableFuture<>());
        Event event = new Event(TOKEN_CONSUME_REQUEST, new Object[] { tokenID, correlationId });
        queue.publish(event);
        return correlations.get(correlationId).join();
    }

    public void handleTokenConsumeFinished(Event ev){
        TokenActionResponse response = ev.getArgument(0, TokenActionResponse.class);
        var correlationId = ev.getArgument(1, CorrelationId.class);
        correlations.get(correlationId).complete(response);
    }

    public void handleTokenVerifyFinished(Event ev){
        TokenActionResponse response = ev.getArgument(0, TokenActionResponse.class);
        var correlationId = ev.getArgument(1, CorrelationId.class);
        correlations.get(correlationId).complete(response);
    }

    public void handleGetCustomerFinished(Event ev){
        var customerRetrieveResponse = ev.getArgument(0, CustomerRetrieveResponse.class);
        var correlationId = ev.getArgument(1, CorrelationId.class);
        correlationsCustomer.get(correlationId).complete(customerRetrieveResponse);
    }

    public void handleGetMerchantFinished(Event ev){
    MerchantRetrieveResponse merchantRetrieveResponse = ev.getArgument(0, MerchantRetrieveResponse.class);
    var correlationId = ev.getArgument(1, CorrelationId.class);
    correlationsMerchant.get(correlationId).complete(merchantRetrieveResponse);
}

    public void handlePaymentFinished(Event ev){
        var correlationId = ev.getArgument(0, CorrelationId.class);
        correlationsPaymentRegistration.get(correlationId).complete(new PaymentResponse());
    }

}
