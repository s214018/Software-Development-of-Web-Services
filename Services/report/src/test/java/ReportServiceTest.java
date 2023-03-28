/**
 *
 * @author Julia Makulec
 * @author Mads Klynderud (s215955)
 */

import domain.Payment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import responses.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportServiceTest {
    private final Payment dummyPayment = new Payment(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            100
    );
    private static ReportService service;

    @BeforeAll
    static void setup() {
        service = new ReportService();
    }

    @AfterEach
    void cleanUpReportService() {
        service.paymentDatabase.clear();
    }

    @Test
    @DisplayName("Payment should be saved to database")
    void logPayment() {
        service.logPayment(dummyPayment);
        assertTrue(service.paymentDatabase.contains(dummyPayment));
    }

    @Test
    @DisplayName("Customer report single payment")
    void getCustomerReportSingle() {
        UUID customerID = UUID.randomUUID();
        UUID merchantID = UUID.randomUUID();
        UUID token = UUID.randomUUID();
        double amount = 100;
        Payment payment = new Payment(customerID, merchantID, token, amount);
        service.logPayment(payment);
        CustomerResponse response = service.getCustomerReport(customerID);
        assertEquals(CustomerPaymentResponse.class, response.getPaymentResponseList().get(0).getClass());
        assertEquals(1, response.getPaymentResponseList().size());
        assertEquals(amount, response.getPaymentResponseList().get(0).getAmount());
        assertEquals(merchantID, response.getPaymentResponseList().get(0).getMerchantID());
        assertEquals(token, response.getPaymentResponseList().get(0).getToken());
    }

    @Test
    @DisplayName("Customer report multiple payments")
    void getCustomerReport() {
        UUID customerID = UUID.randomUUID();
        int numberOfPayments = 5;
        List<Payment> customerPayments = generateAndLogDummyPayments(numberOfPayments, customerID, null);
        CustomerResponse response = service.getCustomerReport(customerID);
        assertEquals(customerPayments.size(), response.getPaymentResponseList().size()
                , "The size of customer reported payments should match the payments");
        assertEquals(customerPayments.stream().map(Payment::getToken).collect(Collectors.toList())
                , response.getPaymentResponseList().stream().map(CustomerPaymentResponse::getToken).collect(Collectors.toList()),
                "The list of tokens should match");
        assertEquals(customerPayments.stream().map(Payment::getAmount).collect(Collectors.toList())
                , response.getPaymentResponseList().stream().map(CustomerPaymentResponse::getAmount).collect(Collectors.toList()),
                "The list of amounts should match");
        assertEquals(customerPayments.stream().map(Payment::getMerchantID).collect(Collectors.toList())
                , response.getPaymentResponseList().stream().map(CustomerPaymentResponse::getMerchantID).collect(Collectors.toList()),
                "The list of merchantID should match");
        assertTrue(customerPayments.stream().map(CustomerPaymentResponse::new)
                        .allMatch(customerPaymentResponse -> response.getPaymentResponseList()
                                .stream().anyMatch(r -> r.getAmount() == customerPaymentResponse.getAmount()
                                        && r.getMerchantID() == customerPaymentResponse.getMerchantID()
                                        && r.getToken() == customerPaymentResponse.getToken()
                                ))
                , "All customer responses should match all fields");
    }

    @Test
    @DisplayName("Merchant report single payment")
    void getMerchantReportSingle() {
        UUID customerID = UUID.randomUUID();
        UUID merchantID = UUID.randomUUID();
        UUID token = UUID.randomUUID();
        double amount = 100;
        Payment payment = new Payment(customerID, merchantID, token, amount);
        service.logPayment(payment);
        List<MerchantResponse> response = service.getMerchantReport(merchantID);
        assertEquals(MerchantResponse.class, response.get(0).getClass());
        assertEquals(1, response.size());
        assertEquals(amount, response.get(0).getAmount());
        assertEquals(token, response.get(0).getToken());
    }

    @Test
    @DisplayName("Merchant report multiple payments")
    void getMerchantReport() {
        UUID merchantID = UUID.randomUUID();
        int numberOfPayments = 5;
        List<Payment> merchantPayments = generateAndLogDummyPayments(numberOfPayments, null, merchantID);
        List<MerchantResponse> response = service.getMerchantReport(merchantID);
        assertEquals(merchantPayments.size(), response.size()
                , "The size of merchant reported payments should match the payments");
        assertEquals(merchantPayments.stream().map(Payment::getToken).collect(Collectors.toList())
                , response.stream().map(MerchantResponse::getToken).collect(Collectors.toList()),
                "The list of tokens should match");
        assertEquals(merchantPayments.stream().map(Payment::getAmount).collect(Collectors.toList())
                , response.stream().map(MerchantResponse::getAmount).collect(Collectors.toList()),
                "The list of amounts should match");
        assertTrue(merchantPayments.stream().map(MerchantResponse::new)
                        .allMatch(customerResponse -> response
                                .stream().anyMatch(r -> r.getAmount() == customerResponse.getAmount()
                                        && r.getToken() == customerResponse.getToken()
                                ))
                , "All merchant responses should match all fields");
    }

    @Test
    @DisplayName("Manager report dummy payments")
    void getManagerResponse() {
        int numberOfPayments = 5;
        List<Payment> payments = generateAndLogDummyPayments(numberOfPayments, null, null);
        double totalAmount = payments.stream().mapToDouble(Payment::getAmount).sum();
        ManagerResponse response = service.getManagerReport();
        assertEquals(payments.size(), response.getPaymentList().size()
                , "The size of reported payments should match the payments");
        assertEquals(payments.stream().map(Payment::getCustomerID).collect(Collectors.toList())
                , response.getPaymentList().stream().map(ManagerPayment::getCustomerID).collect(Collectors.toList()),
                "The list of customerID should match");
        assertEquals(payments.stream().map(Payment::getAmount).collect(Collectors.toList())
                , response.getPaymentList().stream().map(ManagerPayment::getAmount).collect(Collectors.toList()),
                "The list of amounts should match");
        assertEquals(payments.stream().map(Payment::getMerchantID).collect(Collectors.toList())
                , response.getPaymentList().stream().map(ManagerPayment::getMerchantID).collect(Collectors.toList()),
                "The list of merchantID should match");
        assertTrue(payments.stream().map(ManagerPayment::new).allMatch(payment -> response.getPaymentList()
                        .stream().anyMatch(r -> r.getAmount() == payment.getAmount()
                                && r.getMerchantID() == payment.getMerchantID()
                                && r.getCustomerID() == payment.getCustomerID()
                        ))
                , "All manager payment responses should match payment fields");
        assertEquals(totalAmount, response.getTotalPaymentAmount());
    }

    @Test
    @DisplayName("Manager report two payments")
    void getManagerReportTwoPayments() {
        UUID customerID1 = UUID.randomUUID();
        UUID customerID2 = UUID.randomUUID();
        UUID merchantID1 = UUID.randomUUID();
        UUID merchantID2 = UUID.randomUUID();
        UUID token1 = UUID.randomUUID();
        UUID token2 = UUID.randomUUID();
        double amount1 = 100;
        double amount2 = 100;
        Payment payment1 = new Payment(customerID1, merchantID1, token1, amount1);
        Payment payment2 = new Payment(customerID2, merchantID2, token2, amount2);
        service.logPayment(payment1);
        service.logPayment(payment2);
        ManagerResponse response = service.getManagerReport();
        assertEquals(ManagerResponse.class, response.getClass());
        assertEquals(ManagerPayment.class, response.getPaymentList().get(0).getClass());
        assertEquals(2, response.getPaymentList().size());
        assertEquals(200, response.getTotalPaymentAmount());
    }

    private List<Payment> generateAndLogDummyPayments(int number, UUID customerID, UUID merchantId) {
        List<Payment> payments = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            Payment payment = new Payment(
                    customerID != null ? customerID : UUID.randomUUID(),
                    merchantId != null ? merchantId : UUID.randomUUID(),
                    UUID.randomUUID(),
                    Math.random() * 10
            );
            payments.add(payment);
            service.logPayment(payment);
        }
        return payments;
    }
}