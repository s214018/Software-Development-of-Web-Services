package stepdefinitions;

import domain.responses.CustomerCreationResponse;
import domain.responses.TokensResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import services.CustomerFacadeService;

import javax.ws.rs.WebApplicationException;
import java.net.HttpURLConnection;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
/**
 *
 * @author Mads Klynderud (s215955)
 *
 */
public class TokenSteps {

    private final CustomerFacadeService customerFacadeService = new CustomerFacadeService();
    private CustomerCreationResponse customerCreationResponse;
    private int errorCode;

    @Given("a customer with random UUID and bank account")
    public void aCustomerWithRandomUUID() {
        String bankID = UUID.randomUUID().toString();
        customerCreationResponse = customerFacadeService.createCustomer(bankID);
    }

    @When("the customer requests {int} tokens")
    public void theCustomerRequestsTokens(int numOfTokens) {
        TokensResponse tokensResponse = new TokensResponse();
        try {
            tokensResponse = customerFacadeService.createTokens(customerCreationResponse.getCustomer().getId(), numOfTokens);
        } catch (WebApplicationException exception) {
            errorCode = exception.getResponse().getStatus();
        }
        customerCreationResponse.getCustomer().setTokens(tokensResponse.getTokens());
    }

    @Then("{int} tokens is returned")
    public void tokensIsReturned(int num) {
        assertEquals(num, customerCreationResponse.getCustomer().getTokens().size());
    }

    @Then("bad request error is returned")
    public void badRequestErrorIsReturned() {
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, errorCode);
    }
}
