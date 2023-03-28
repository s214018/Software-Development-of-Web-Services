/**
 *
 * @author Julia Makulec
 *
 */
package stepdefinitions;

import domain.responses.CustomerDeregisterResponse;
import domain.responses.MerchantDeregisterResponse;
import dtu.ws.fastmoney.*;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import domain.responses.CustomerCreationResponse;
import domain.responses.MerchantCreationResponse;
import services.CustomerFacadeService;
import services.MerchantFacadeService;

import javax.ws.rs.WebApplicationException;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static domain.messages.EventMessages.ACCOUNT_DEREGISTER_SUCCESSFUL;
import static org.junit.Assert.*;

public class RegistrationSteps {
    private final MerchantFacadeService merchantFacadeService = new MerchantFacadeService();
    private final CustomerFacadeService customerFacadeService = new CustomerFacadeService();
    private final BankService bankService = new BankServiceService().getBankServicePort();
    private String bankAccountID;
    private CustomerCreationResponse customerCreationResponse;
    private MerchantCreationResponse merchantCreationResponse;

    private MerchantDeregisterResponse merchantDeregisterResponse;
    private CustomerDeregisterResponse customerDeregisterResponse;
    private int errorCode;
    private List<String> bankAccountIDsCreated = new ArrayList<>();
    private Account bankAccount;

    @After("@DeleteAccountsAfter")
    public void deleteBankAccounts() throws BankServiceException_Exception{
        for (String s : bankAccountIDsCreated) {
            bankService.retireAccount(s);
        }
        bankAccountIDsCreated = new ArrayList<>();
    }
    @Given("a bank account with ID {string}")
    public void aBankAccountWithID(String bankAccountID){
        this.bankAccountID = bankAccountID;
    }

    @When("a new customer is created with the bank account")
    public void aNewCustomerIsCreatedWithTheBankAccount() {
        this.customerCreationResponse = customerFacadeService.createCustomer(bankAccountID); }


    @Then("the request for a new customer has succeded")
    public void theRequestForANewCustomerHasSucceded() {
        assertEquals(bankAccountID, this.customerCreationResponse.getCustomer().getBankAccountID());
    }

    @When("a new merchant is created with the bank account")
    public void aNewMerchantIsCreatedWithTheBankAccount() {
        merchantCreationResponse = merchantFacadeService.createMerchant(bankAccountID);
    }

    @Then("the request for a new merchant has succeded")
    public void theRequestForANewMerchantHasSucceded() {
        assertEquals(bankAccountID, merchantCreationResponse.getMerchant().getBankAccountID());
    }

    @Given("a newly created customer with bank account ID {string}")
    public void aNewlyCreatedCustomer(String bankAccountID) { customerCreationResponse = customerFacadeService.createCustomer(bankAccountID);}

    @Then("the response returns bad request")
    public void theResponseReturnsBadRequest() {
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, errorCode);
    }

    @When("I register a bank account with balance {int}")
    public void iRegisterABankAccountWithBalance(int balance) throws BankServiceException_Exception {
        User user = new User();
        user.setCprNumber("290388-1122");
        user.setFirstName("Test");
        user.setLastName("Testesensen");

        BigDecimal balanceObject = new BigDecimal(balance);

        String newBankAccountID = bankService.createAccountWithBalance(user, balanceObject);
        bankAccountIDsCreated.add(newBankAccountID);
    }

    @Then("The bank account exists")
    public void theBankAccountExists() throws BankServiceException_Exception{
        bankAccount = bankService.getAccount(bankAccountIDsCreated.get(0));
        assertEquals(bankAccountIDsCreated.get(0), bankAccount.getId());
    }

    @And("The bank account has balance {int}")
    public void theBankAccountHasBalance(int balance) {
        BigDecimal balanceObject = new BigDecimal(balance);
        assertEquals(balanceObject, bankAccount.getBalance());
    }


    @When("a new customer registration is attempted")
    public void aNewCustomerRegistrationIsAttempted() {
        try {
            this.customerCreationResponse = customerFacadeService.createCustomer(this.bankAccountID);
        } catch (WebApplicationException ex) {
            errorCode = ex.getResponse().getStatus();
        }
    }

    @When("a new merchant registration is attempted")
    public void aNewMerchantRegistrationIsAttempted() {
        try {
            this.merchantCreationResponse = merchantFacadeService.createMerchant(this.bankAccountID);
        } catch (WebApplicationException ex) {
            errorCode = ex.getResponse().getStatus();
        }
    }

    @When("the merchant requests deregistration")
    public void theMerchantRequestsDeregistration() {
        merchantDeregisterResponse = this.merchantFacadeService.deregisterMerchant(merchantCreationResponse.getMerchant().getId());
    }

    @Then("the request deregistering merchant is successful")
    public void theRequestDeregisteringMerchantIsSuccessful() {
        assertEquals(merchantDeregisterResponse.getMessage(), ACCOUNT_DEREGISTER_SUCCESSFUL);
    }

    @When("a merchant with ID {string} requests deregistration")
    public void aMerchantWithRandomIDRequestsDeregistration(String merId) {
        try {
            this.merchantDeregisterResponse = this.merchantFacadeService.deregisterMerchant(UUID.fromString(merId));
        } catch (WebApplicationException exception) {
            this.errorCode = exception.getResponse().getStatus();
        }
    }

    @When("the customer requests deregistration")
    public void theCustomerRequestsDeregistration() {
        customerDeregisterResponse = this.customerFacadeService.deregisterCustomer(customerCreationResponse.getCustomer().getId());
    }

    @Then("the request deregistering customer is successful")
    public void theRequestDeregisteringCustomerIsSuccessful() {
        assertEquals(ACCOUNT_DEREGISTER_SUCCESSFUL, customerDeregisterResponse.getMessage());
    }

    @When("a customer with random ID requests deregistration")
    public void aCustomerWithRandomIDRequestsDeregistration() {
        try {
            this.customerDeregisterResponse = this.customerFacadeService.deregisterCustomer(UUID.randomUUID());
        } catch (WebApplicationException exception) {
            this.errorCode = exception.getResponse().getStatus();
        }
    }
}
