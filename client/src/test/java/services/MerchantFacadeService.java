package services;

import domain.requests.PaymentRequest;
import domain.responses.*;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.UUID;

import javax.ws.rs.client.*;
/**
 *
 * @author Mads Klynderud (s215955)
 *
 */
public class MerchantFacadeService {
    private final Client client = ClientBuilder.newClient();
    private final WebTarget webTarget = client.target("http://localhost:8000/merchant");

    public MerchantCreationResponse createMerchant(String bankAccountID) {
        Invocation.Builder builder = webTarget.queryParam("bankAccountID", bankAccountID).request();
        return builder.post(Entity.json(null), MerchantCreationResponse.class);
    }
    public PaymentResponse makePayment(PaymentRequest paymentRequest) {
        Invocation.Builder builder = webTarget.path("payment").request(MediaType.APPLICATION_JSON);
        return  builder.post(Entity.json(paymentRequest), PaymentResponse.class);
    }

    public MerchantDeregisterResponse deregisterMerchant(UUID merchantID) {
        WebTarget target = webTarget.queryParam("merchantID", merchantID);
        Invocation.Builder builder = target.request();
        return builder.delete(MerchantDeregisterResponse.class);
    }

    public ArrayList<MerchantResponse> getReport(UUID merchantID) {
        WebTarget target = webTarget.path(String.valueOf(merchantID))
                .path("report");
        Response response = target.request().get(Response.class);
        return response.readEntity(new GenericType<>() {});
    }
}

