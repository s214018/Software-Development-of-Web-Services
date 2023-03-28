/**
 *
 * @author Julia Makulec
 *
 */
package messaging.domain;

public class EventTypeResource {
public static final String MANAGER_REPORT_RESPONSE = "ManagerReportResponse";
public static final String MANAGER_REPORT_REQUEST = "ManagerReportRequest";
public static final String CUSTOMER_REPORT_RESPONSE = "CustomerReportResponse";
public static final String CUSTOMER_REPORT_REQUEST = "CustomerReportRequest";
public static final String MERCHANT_REPORT_RESPONSE = "MerchantReportResponse";
public static final String MERCHANT_REPORT_REQUEST = "MerchantReportRequest";
public static final String REQUEST_REGISTER_PAYMENT = "RegisterPaymentRequest";
public static final String PAYMENT_REGISTERED = "PaymentRegistrationFinished";

public static final String CUSTOMER_REGISTER_REQUEST = "RegisterCustomer";
public static final String CUSTOMER_REGISTER_RESPONSE = "RegisterCustomerFinished";
    public static final String CUSTOMER_DEREGISTER_REQUEST = "DeregisterCustomer";
    public static final String CUSTOMER_DEREGISTER_RESPONSE = "DeregisterCustomerFinished";
public static final String MERCHANT_REGISTER_REQUEST = "RegisterMerchant";
public static final String MERCHANT_REGISTER_RESPONSE = "RegisterMerchantFinished";
    public static final String MERCHANT_DEREGISTER_REQUEST = "DeregisterMerchant";
    public static final String MERCHANT_DEREGISTER_RESPONSE = "DeregisterMerchantFinished";
public static final String TOKEN_GENERATION_REQUEST = "GenerateCustomerToken";
public static final String TOKEN_GENERATION_RESPONSE = "GenerateCustomerTokenFinished";
public static final String EXECUTE_PAYMENT_REQUEST = "RequestPayment";
public static final String EXECUTE_PAYMENT_RESPONSE = "RequestPaymentFinished";

public static final String TOKEN_CONSUME_REQUEST = "TokenConsumeRequest";
public static final String TOKEN_CONSUME_FINISHED = "TokenConsumeFinished";
public static final String TOKEN_VERIFY_REQUEST = "TokenVerifyRequest";
public static final String TOKEN_VERIFY_FINISHED = "TokenVerifyFinished";
public static final String GET_CUSTOMER_REQUEST = "GetCustomerRequest";
public static final String GET_CUSTOMER_FINISHED = "GetCustomerFinished";
public static final String GET_MERCHANT_REQUEST = "GetMerchantRequest";
public static final String GET_MERCHANT_FINISHED = "GetMerchantFinished";
}
