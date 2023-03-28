/**
 *
 * @author Hjalte Bøgehave
 *
 */

package stepdefinitions;

import domain.requests.PaymentRequest;
import domain.responses.*;
import dtu.ws.fastmoney.*;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import services.CustomerFacadeService;
import services.ManagerFacadeService;
import services.MerchantFacadeService;
import java.util.Random;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReportingSteps {
    private final CustomerFacadeService customerFacadeService = new CustomerFacadeService();
    private final MerchantFacadeService merchantFacadeService = new MerchantFacadeService();
    private final ManagerFacadeService managerFacadeService = new ManagerFacadeService();

    private CustomerCreationResponse customerCreationResponse;
    private MerchantCreationResponse merchantCreationResponse;
    private CustomerResponse customerPurchases;
    private ArrayList<MerchantResponse> merchantPurchases;
    private ManagerResponse managerReport;
    private final BankService bankService = new BankServiceService().getBankServicePort();
    private PaymentRequest paymentRequest;

    private final List<String> bankAccountIDsCreated = new ArrayList<>();


    public String randomCPR() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(rand.nextInt(10));
        }
        sb.append("-");
        for (int i = 0; i < 4; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }

    @After("@DeleteAfterReporting")
    public void deleteBankAccounts() throws BankServiceException_Exception {
        for (String s : bankAccountIDsCreated) {
            bankService.retireAccount(s);
        }
        //bankAccountIDsCreated = new ArrayList<>();
    }

    @When("the customer requests report")
    public void theCustomerRequestsReport() {
        customerPurchases = customerFacadeService.getReport(customerCreationResponse.getCustomer().getId());
    }


    @When("the merchant requests report")
    public void theMerchantRequestsReport() {
        merchantPurchases = merchantFacadeService.getReport(merchantCreationResponse.getMerchant().getId());
    }

    @When("the manager requests report")
    public void theManagerRequestsReport() {
        managerReport = managerFacadeService.getManagerReportResponse();
    }

    @Given("a registered customer with bank account with balance {int}")
    public void aRegisteredCustomerWithBankAccountWithBalance(int balance) throws BankServiceException_Exception {
        User user = new User();

        user.setCprNumber(randomCPR());
        user.setFirstName("ReportingCustomer");
        user.setLastName("ForGroup18");
        BigDecimal balanceObject = new BigDecimal(balance);
        String newBankAccountID = bankService.createAccountWithBalance(user, balanceObject);
        this.customerCreationResponse = this.customerFacadeService.createCustomer(newBankAccountID);
        bankAccountIDsCreated.add(newBankAccountID);

    }

    @And("a registered merchant with bank account with balance {int}")
    public void aRegisteredMerchantWithBankAccountWithBalance(int balance) throws BankServiceException_Exception {
        User user = new User();
        user.setCprNumber(randomCPR());
        user.setFirstName("ReportingMerchant");
        user.setLastName("ForGroup18");
        BigDecimal balanceObject = new BigDecimal(balance);
        String newBankAccountID = bankService.createAccountWithBalance(user, balanceObject);
        this.merchantCreationResponse = this.merchantFacadeService.createMerchant(newBankAccountID);
        bankAccountIDsCreated.add(newBankAccountID);
    }

    @And("the customer requested {int} tokens")
    public void theCustomerRequestedTokens(int numTokens) {
        customerCreationResponse.getCustomer()
                .setTokens(customerFacadeService.createTokens(customerCreationResponse.getCustomer().getId(), numTokens).getTokens());
    }

    @When("a payment is made for {int}")
    public void aPaymentIsMadeFor(int amount) {
        PaymentRequest p = new PaymentRequest();
        p.setMerchantID(merchantCreationResponse.getMerchant().getId());
        p.setAmount(amount);
        p.setToken(customerCreationResponse.getCustomer().getTokens().get(0).getTokenID());
        merchantFacadeService.makePayment(p);
        paymentRequest = p;
    }

    @Then("the customer balance is {int} kr")
    public void theCustomerBalanceIsKr(int balance) throws BankServiceException_Exception {
        Account account = bankService.getAccount(customerCreationResponse.getCustomer().getBankAccountID());
        BigDecimal expectedBalance = new BigDecimal(balance);
        assertEquals(expectedBalance, account.getBalance());
    }

    @And("the merchant balance is {int} kr")
    public void theMerchantBalanceIsKr(int balance) throws BankServiceException_Exception {
        Account account = bankService.getAccount(merchantCreationResponse.getMerchant().getBankAccountID());
        BigDecimal expectedBalance = new BigDecimal(balance);
        assertEquals(expectedBalance, account.getBalance());
    }

    @Then("customer purchases should have {int} entry")
    public void customerPurchasesShouldHaveEntry(int size) {
        assertEquals(size, customerPurchases.getPaymentResponseList().size());
    }

    @And("merchant id is in the customer response")
    public void merchantIdIsInTheCustomerResponse() {
        assertTrue(customerPurchases.getPaymentResponseList().stream().anyMatch(r -> r.getMerchantID().equals(merchantCreationResponse.getMerchant().getId())));
    }

    @Then("merchant payments should have {int} entries")
    public void merchantPaymentsShouldHaveEntries(int size) {
        assertEquals(size, merchantPurchases.size());
    }

    @And("customer token is in the response")
    public void customerTokenIsInTheResponse() {
        assertTrue(merchantPurchases.stream().anyMatch(r -> r.getToken().equals(customerCreationResponse.getCustomer().getTokens().get(0).getTokenID())));
    }

    @Then("manager report should contain the payment entry")
    public void managerReportShouldContainThePaymentEntry() {
        assertTrue(managerReport.getPaymentList().stream().anyMatch(p -> p.getAmount() == paymentRequest.getAmount()
            && p.getMerchantID().equals(paymentRequest.getMerchantID())
                && p.getCustomerID().equals(customerCreationResponse.getCustomer().getId())
        ));
    }
}
