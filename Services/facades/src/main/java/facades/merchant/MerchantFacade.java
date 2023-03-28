package facades.merchant;

import domain.merchant.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

import static messaging.domain.EventMessages.*;
/**
 *
 * @author Muaz Ahmed
 *
 */

@Path("/merchant")
public class MerchantFacade {
    MerchantFacadeService service = new MerchantFacadeServiceFactory().getService();


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MerchantCreationResponse createMerchant(@QueryParam("bankAccountID") String bankAccountID) {
        MerchantCreationResponse res = service.createMerchantRequest(bankAccountID);
        if (!res.getMessage().equals(ACCOUNT_GENERATION_SUCCESSFUL))
            throw new WebApplicationException(res.getMessage(), Response.Status.BAD_REQUEST);
        return res;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public MerchantDeregisterResponse deleteMerchant(@QueryParam("merchantID") UUID merchantID) {
        MerchantDeregisterResponse res = service.deleteMerchant(merchantID);
        if (!res.getMessage().equals(ACCOUNT_DEREGISTER_SUCCESSFUL))
            throw new WebApplicationException(res.getMessage(), Response.Status.BAD_REQUEST);
        return res;
    }

    /*
    * Merchant report
    */

    @GET
    @Path("/{id}/report")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MerchantResponse> getMerchantReport(@PathParam("id") UUID merchantID) { return service.getMerchantReportRequest(merchantID); }

    @POST
    @Path("/payment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    public PaymentResponse makePayment(PaymentRequest paymentRequest) {
        PaymentResponse response = service.makePaymentRequest(paymentRequest);
        if (!response.getMessage().equals(PAYMENT_SUCCESSFUL)) throw new WebApplicationException(response.getMessage(), Response.Status.BAD_REQUEST);
        return response;
    }
}