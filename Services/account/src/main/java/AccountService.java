import domain.CorrelationId;
import domain.Customer;
import domain.Merchant;
import lombok.NoArgsConstructor;
import messaging.Event;
import messaging.MessageQueue;
import responses.*;

import java.util.ArrayList;
import java.util.UUID;

import static messaging.domain.EventMessages.*;
import static messaging.domain.EventTypeResource.*;

/**
 *
 * @author Muaz Ahmed
 *
 */
@NoArgsConstructor
public class AccountService {
    private final ArrayList<Customer> customerList = new ArrayList<>();
    private final ArrayList<Merchant> merchantList = new ArrayList<>();
    MessageQueue queue;
    public AccountService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler(GET_CUSTOMER_REQUEST, this::handleGetCustomerRequest);
        this.queue.addHandler(GET_MERCHANT_REQUEST, this::handleGetMerchantRequest);
        this.queue.addHandler(CUSTOMER_REGISTER_REQUEST, this::HandleRegisterCustomerRequest);
        this.queue.addHandler(MERCHANT_REGISTER_REQUEST, this::HandleRegisterMerchantRequest);
        this.queue.addHandler(CUSTOMER_DEREGISTER_REQUEST, this::HandleDeregisterCustomerRequest);
        this.queue.addHandler(MERCHANT_DEREGISTER_REQUEST, this::HandleDeregisterMerchantRequest);
    }

    private void HandleRegisterMerchantRequest(Event event) {
        var correlationID = event.getArgument(1, CorrelationId.class);
        var bankAccountID = event.getArgument(0, String.class);
        MerchantCreationResponse response = createMerchant(bankAccountID);
        Event e = new Event(MERCHANT_REGISTER_RESPONSE, new Object[] {response, correlationID});
        this.queue.publish(e);
    }

    private void HandleRegisterCustomerRequest(Event event) {
        var correlationID = event.getArgument(1, CorrelationId.class);
        var bankAccountID = event.getArgument(0, String.class);
        CustomerCreationResponse response = createCustomer(bankAccountID);
        Event e = new Event(CUSTOMER_REGISTER_RESPONSE, new Object[] {response, correlationID});
        this.queue.publish(e);
    }

    private void HandleDeregisterCustomerRequest(Event event) {
        var correlationID = event.getArgument(1, CorrelationId.class);
        var customerID = event.getArgument(0, UUID.class);
        CustomerDeregisterResponse response = deleteCustomer(customerID);
        Event e = new Event(CUSTOMER_DEREGISTER_RESPONSE, new Object[] {response, correlationID});
        this.queue.publish(e);
    }

    private void HandleDeregisterMerchantRequest(Event event) {
        var correlationID = event.getArgument(1, CorrelationId.class);
        var merchantID = event.getArgument(0, UUID.class);
        MerchantDeregisterResponse response = deleteMerchant(merchantID);
        Event e = new Event(MERCHANT_DEREGISTER_RESPONSE, new Object[] {response, correlationID});
        this.queue.publish(e);
    }

    private MerchantDeregisterResponse deleteMerchant(UUID merchantID) {
        Merchant merchant = merchantList.stream().filter(m -> m.getId().equals(merchantID))
                .findAny()
                .orElse(null);
        if (merchant == null) {
            return new MerchantDeregisterResponse(MERCHANT_RETRIEVAL_ERROR);
        }
        merchantList.remove(merchant);
        return new MerchantDeregisterResponse();
    }

    private CustomerDeregisterResponse deleteCustomer(UUID customerID) {
        Customer customer = customerList.stream().filter(c -> c.getId().equals(customerID))
                .findAny()
                .orElse(null);
        if (customer == null) {
            return new CustomerDeregisterResponse(CUSTOMER_RETRIEVAL_ERROR);
        }
        customerList.remove(customer);
        return new CustomerDeregisterResponse();
    }

    public CustomerCreationResponse createCustomer(String bankAccountID) {
        if(bankAccountID == null || bankAccountID.isBlank()){
            return new CustomerCreationResponse(NO_BANK_ACC_PROVIDED);
        }
        Customer customer = new Customer(bankAccountID, UUID.randomUUID());
        customerList.add(customer);

        return new CustomerCreationResponse(customer);
    }

    public CustomerRetrieveResponse getCustomer(UUID customerID) {
        Customer customer = customerList.stream().filter(c -> c.getId()
                        .equals(customerID))
                .findAny()
                .orElse(null);

        if(customer == null){
            return new CustomerRetrieveResponse(CUSTOMER_RETRIEVAL_ERROR);
        }

        return new CustomerRetrieveResponse(customer);
    }

    public MerchantCreationResponse createMerchant(String bankAccountID) {
        if(bankAccountID == null || bankAccountID.isEmpty()){
            return new MerchantCreationResponse(NO_BANK_ACC_PROVIDED);
        }
        Merchant merchant = new Merchant(bankAccountID, UUID.randomUUID());
        merchantList.add(merchant);
        return new MerchantCreationResponse(merchant);
    }

    public MerchantRetrieveResponse getMerchant(UUID merchantID) {
        Merchant merchant = merchantList.stream().filter(c -> c.getId()
                        .equals(merchantID))
                .findAny()
                .orElse(null);

        if(merchant == null){
            return new MerchantRetrieveResponse(MERCHANT_RETRIEVAL_ERROR);
        }

        return new MerchantRetrieveResponse(merchant);
    }


    public void handleGetCustomerRequest(Event ev){
        var customerID = ev.getArgument(0, UUID.class);
        var correlationId = ev.getArgument(1, CorrelationId.class);

        var customer = getCustomer(customerID);
        Event event = new Event(GET_CUSTOMER_FINISHED, new Object[] {customer, correlationId});
        queue.publish(event);
    }

    public void handleGetMerchantRequest(Event ev){
        var merchantID = ev.getArgument(0, UUID.class);
        var correlationId = ev.getArgument(1, CorrelationId.class);
        MerchantRetrieveResponse merchantRetrieveResponse = getMerchant(merchantID);
        Event event = new Event(GET_MERCHANT_FINISHED, new Object[] {merchantRetrieveResponse, correlationId});
        queue.publish(event);
    }
}
