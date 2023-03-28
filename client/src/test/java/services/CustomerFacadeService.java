package services;

/**
 *
 * @author Muaz Ahmed
 *
 */
import domain.requests.TokenRequest;
import domain.responses.CustomerDeregisterResponse;
import domain.responses.CustomerResponse;
import domain.responses.TokensResponse;
import domain.responses.CustomerCreationResponse;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

public class CustomerFacadeService {
    private final Client client = ClientBuilder.newClient();
    private final WebTarget webTargetCustomers = client.target("http://localhost:8000/customer");


    public TokensResponse createTokens(UUID customerID, int numberOfTokens) {
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setCustomerID(customerID);
        tokenRequest.setNumberOfTokens(numberOfTokens);

        WebTarget target = webTargetCustomers
                .path("token");

        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
        return builder.post(Entity.json(tokenRequest), TokensResponse.class);
    }

    public CustomerCreationResponse createCustomer(String bankAccountID) {
        WebTarget target = webTargetCustomers.queryParam("bankAccountID", bankAccountID);
        Invocation.Builder builder = target.request();
        return builder.post(Entity.json(null), CustomerCreationResponse.class);
    }

    public CustomerDeregisterResponse deregisterCustomer(UUID customerID) {
        WebTarget target = webTargetCustomers.queryParam("customerID", customerID);
        Invocation.Builder builder = target.request();
        return builder.delete(CustomerDeregisterResponse.class);
    }

    public CustomerResponse getReport(UUID customerID) {
        WebTarget target = webTargetCustomers.path(String.valueOf(customerID)).path("report");
        Invocation.Builder builder = target.request();
        return builder.get(CustomerResponse.class);
    }

}
