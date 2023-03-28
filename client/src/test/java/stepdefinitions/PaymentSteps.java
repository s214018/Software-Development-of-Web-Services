package stepdefinitions;

import domain.requests.PaymentRequest;
import domain.responses.CustomerCreationResponse;
import domain.responses.MerchantCreationResponse;
import domain.responses.PaymentResponse;
import domain.responses.TokensResponse;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import dtu.ws.fastmoney.User;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import services.CustomerFacadeService;
import services.MerchantFacadeService;

import javax.ws.rs.WebApplicationException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static domain.messages.EventMessages.PAYMENT_SUCCESSFUL;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Muaz Ahmed
 * @author Gustav Nilsson Pedersen - s174562
 */

public class PaymentSteps {
    private final CustomerFacadeService customerFacadeService = new CustomerFacadeService();
    private final MerchantFacadeService merchantFacadeService = new MerchantFacadeService();
    private final BankService bankService = new BankServiceService().getBankServicePort();

    private final List<String> bankAccountIDsCreated = new ArrayList<>();
    private CustomerCreationResponse customerCreationResponse;
    private MerchantCreationResponse merchantCreationResponse;
    private PaymentResponse paymentResponse;
    private int errorCode;

    /**
     * @author Gustav Nilsson Pedersen - s174562
     */
    @After("@DeleteAccountsAfter")
    public void deleteBankAccounts() throws BankServiceException_Exception {
        for (String s : bankAccountIDsCreated) {
            bankService.retireAccount(s);
        }
        //bankAccountIDsCreated = new ArrayList<>();
    }

    /**
     * @author Gustav Nilsson Pedersen - s174562
     */
    @Given("a customer with a bank account with balance {int}")
    public void aCustomerWithABankAccountWithBalance(int balance) throws BankServiceException_Exception{
        User user = new User();
        user.setCprNumber("300000-1025");
        user.setFirstName("Maryla");
        user.setLastName("Rodowicz");

        BigDecimal balanceObject = new BigDecimal(balance);

        String newBankAccountID = bankService.createAccountWithBalance(user, balanceObject);
        //System.out.println(newBankAccountID);
        bankAccountIDsCreated.add(newBankAccountID);
    }

    /**
     * @author Muaz Ahmed
     */
    @And("that the customer is registered with DTU Pay")
    public void thatTheCustomerIsRegisteredWithDTUPay() {
        customerCreationResponse = customerFacadeService.createCustomer(bankAccountIDsCreated.get(0));
    }

    /**
     * @author Gustav Nilsson Pedersen - s174562
     */
    @Given("a merchant with a bank account with balance {int}")
    public void aMerchantWithABankAccountWithBalance(int balance) throws BankServiceException_Exception{
        User user = new User();
        user.setCprNumber("100021-1617");
        user.setFirstName("Aleskander");
        user.setLastName("Wielki");

        BigDecimal balanceObject = new BigDecimal(balance);

        String newBankAccountID = bankService.createAccountWithBalance(user, balanceObject);
        //System.out.println(newBankAccountID);
        bankAccountIDsCreated.add(newBankAccountID);
    }

    /**
     * @author Muaz Ahmed
     */
    @And("that the merchant is registered with DTU Pay")
    public void thatTheMerchantIsRegisteredWithDTUPay() {
        merchantCreationResponse = merchantFacadeService.createMerchant(bankAccountIDsCreated.get(1));
    }

    /**
     * @author Gustav Nilsson Pedersen - s174562
     */
    @When("the merchant initiates a payment for {int} kr with the customer token")
    public void theMerchantInitiatesAPaymentForKrByTheCustomer(int amount) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setMerchantID(merchantCreationResponse.getMerchant().getId());
        paymentRequest.setToken(customerCreationResponse.getCustomer().getTokens().get(0).getTokenID());
        paymentRequest.setAmount(amount);
        try {
            paymentResponse = merchantFacadeService.makePayment(paymentRequest);
        } catch (WebApplicationException exception) {
            this.errorCode = exception.getResponse().getStatus();
        }
    }

    /**
     * @author Muaz Ahmed
     */
    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertEquals(PAYMENT_SUCCESSFUL, paymentResponse.getMessage());
    }

    /**
     * @author Muaz Ahmed
     */
    @And("the balance of the customer at the bank is {int} kr")
    public void theBalanceOfTheCustomerAtTheBankIsKr(int balance) throws BankServiceException_Exception{
        var account = bankService.getAccount(customerCreationResponse.getCustomer().getBankAccountID());
        BigDecimal expectedBalance = new BigDecimal(balance);
        assertEquals(expectedBalance, account.getBalance());
    }

    /**
     * @author Gustav Nilsson Pedersen - s174562
     */
    @And("the balance of the merchant at the bank is {int} kr")
    public void theBalanceOfTheMerchantAtTheBankIsKr(int balance) throws BankServiceException_Exception{
        var account = bankService.getAccount(merchantCreationResponse.getMerchant().getBankAccountID());
        BigDecimal expectedBalance = new BigDecimal(balance);
        assertEquals(expectedBalance, account.getBalance());
    }

    /**
     * @author Muaz Ahmed
     */
    @Given("the customer has tokens")
    public void theCustomerHasTokens() {
        TokensResponse tokensResponse = customerFacadeService.createTokens(customerCreationResponse.getCustomer().getId(), 1);
        customerCreationResponse.getCustomer().setTokens(tokensResponse.getTokens());
    }

    /**
     * @author Muaz Ahmed
     */
    @When("the merchant initiates a payment for {int} kr with invalid token")
    public void theMerchantInitiatesAPaymentForIntKrWithInvalidToken(int amount) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setMerchantID(merchantCreationResponse.getMerchant().getId());
        paymentRequest.setToken(UUID.randomUUID());
        paymentRequest.setAmount(amount);
        try {
            paymentResponse = merchantFacadeService.makePayment(paymentRequest);
        } catch (WebApplicationException exception) {
            this.errorCode = exception.getResponse().getStatus();
        }
    }

    /**
     * @author Gustav Nilsson Pedersen - s174562
     */
    @Then("A bad request error is returned")
    public void aBadRequestErrorIsReturned() {
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, errorCode);
    }

    /**
     * @author Gustav Nilsson Pedersen - s174562
     */
    @Given("a merchant {string} {string} with CPR {string} with a bank account with balance {int}")
    public void aMerchantWithCPRWithABankAccountWithBalance(String name, String lastName, String CPR, int balance) throws BankServiceException_Exception {
        User user = new User();
        user.setCprNumber(CPR);
        user.setFirstName(name);
        user.setLastName(lastName);

        BigDecimal balanceObject = new BigDecimal(balance);
        String newBankAccountID = bankService.createAccountWithBalance(user, balanceObject);
        bankAccountIDsCreated.add(newBankAccountID);
    }

    /**
     * @author Muaz Ahmed
     */
    @When("the unregistered merchant initiates a payment for {int} kr with the customer token")
    public void theUnregisteredMerchantInitiatesAPaymentForKrByTheCustomer(int amount) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setMerchantID(UUID.randomUUID());
        paymentRequest.setToken(customerCreationResponse.getCustomer().getTokens().get(0).getTokenID());
        paymentRequest.setAmount(amount);
        try {
            paymentResponse = merchantFacadeService.makePayment(paymentRequest);
        } catch (WebApplicationException exception) {
            this.errorCode = exception.getResponse().getStatus();
        }
    }
}
