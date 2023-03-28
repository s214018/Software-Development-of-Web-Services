/**
 *
 * @author Julia Makulec
 *
 */

import domain.CorrelationId;
import domain.Payment;
import lombok.NoArgsConstructor;
import messaging.Event;
import messaging.MessageQueue;
import responses.CustomerPaymentResponse;
import responses.CustomerResponse;
import responses.ManagerResponse;
import responses.MerchantResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static messaging.domain.EventTypeResource.*;

@NoArgsConstructor
public class ReportService {
    MessageQueue q;
    public final ArrayList<Payment> paymentDatabase = new ArrayList<>();

    public ReportService(MessageQueue q) {
        this.q = q;
        this.q.addHandler(REQUEST_REGISTER_PAYMENT, this::handlePaymentFinishedRequest);
        this.q.addHandler(MANAGER_REPORT_REQUEST, this::handleManagerReportRequest);
        this.q.addHandler(CUSTOMER_REPORT_REQUEST, this::handleCustomerReportRequest);
        this.q.addHandler(MERCHANT_REPORT_REQUEST, this::handleMerchantReportRequest);
    }

    private void handleManagerReportRequest(Event event) {
        var id = event.getArgument(0, CorrelationId.class);
        ManagerResponse response = getManagerReport();
        q.publish(new Event(MANAGER_REPORT_RESPONSE, new Object[] {response, id}));
    }

    private void handleMerchantReportRequest(Event event) {
        var id = event.getArgument(1, CorrelationId.class);
        var merchantID = event.getArgument(0, UUID.class);
        List<MerchantResponse> response = getMerchantReport(merchantID);
        q.publish(new Event(MERCHANT_REPORT_RESPONSE, new Object[] {response, id}));
    }

    private void handleCustomerReportRequest(Event event) {
        var id = event.getArgument(1, CorrelationId.class);
        var customerId = event.getArgument(0, UUID.class);
        CustomerResponse response = getCustomerReport(customerId);
        q.publish(new Event(CUSTOMER_REPORT_RESPONSE, new Object[] {response, id}));
    }

    private void handlePaymentFinishedRequest(Event event) {
        System.out.println("Handle finished payment in ReportService");
        var correlationId = event.getArgument(1, CorrelationId.class);
        var payment = event.getArgument(0, Payment.class);
        logPayment(payment);
        System.out.println("Payment logged");
        q.publish(new Event(PAYMENT_REGISTERED, new Object[] {correlationId}));
    }

    public void logPayment(Payment payment) {
        paymentDatabase.add(payment);
    }


    public CustomerResponse getCustomerReport(UUID customerID) {
        return new CustomerResponse(paymentDatabase.stream().filter(payment -> payment.getCustomerID().equals(customerID)).map(CustomerPaymentResponse::new).collect(Collectors.toList()));
    }

    public List<MerchantResponse> getMerchantReport(UUID merchantID) {
        return paymentDatabase.stream().filter(payment -> payment.getMerchantID().equals(merchantID)).map(MerchantResponse::new).collect(Collectors.toList());
    }

    public ManagerResponse getManagerReport() {
        return new ManagerResponse(paymentDatabase);
    }
}


