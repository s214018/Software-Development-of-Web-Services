/**
 *
 * @author Julia Makulec
 *
 */
package facades.customer;

import domain.TokenRequest;
import domain.customer.CustomerCreationResponse;
import domain.customer.CustomerDeregisterResponse;
import domain.customer.CustomerResponse;
import domain.customer.TokensResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

import static messaging.domain.EventMessages.*;

@Path("/customer")
public class CustomerFacade {


        CustomerFacadeService service = new CustomerFacadeServiceFactory().getService();

        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public CustomerCreationResponse createCustomer(@QueryParam("bankAccountID") String bankAccountID) {
                CustomerCreationResponse res = service.createCustomerRequest(bankAccountID);
                if (!res.getMessage().equals(ACCOUNT_GENERATION_SUCCESSFUL))
                        throw new WebApplicationException(res.getMessage(), Response.Status.BAD_REQUEST);
                return res;
        }


        @POST
        @Path("/token")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public TokensResponse createTokens(TokenRequest tokenRequest) {
                TokensResponse res = service.createTokens(tokenRequest.getCustomerID(), tokenRequest.getNumberOfTokens());
                if (!res.getMessage().equals(TOKEN_GENERATION_SUCCESSFUL))
                        throw new WebApplicationException(res.getMessage(), Response.Status.BAD_REQUEST);
                return res;
        }

        @GET
        @Path("/{id}/report")
        @Produces(MediaType.APPLICATION_JSON)
        public CustomerResponse getCustomerReport(@PathParam("id") UUID customerID) {
                return service.getCustomerReport(customerID);
        }

        @DELETE
        @Produces(MediaType.APPLICATION_JSON)
        public CustomerDeregisterResponse deregisterCustomer(@QueryParam("customerID") UUID customerID) {
                CustomerDeregisterResponse res = service.deregisterCustomer(customerID);
                if (!res.getMessage().equals(ACCOUNT_DEREGISTER_SUCCESSFUL))
                        throw new WebApplicationException(res.getMessage(), Response.Status.BAD_REQUEST);
                return res;
        }

}
