package responses;

import messaging.domain.GenericResponse;

import static messaging.domain.EventMessages.ACCOUNT_DEREGISTER_SUCCESSFUL;

public class MerchantDeregisterResponse extends GenericResponse {
    public MerchantDeregisterResponse() {
        this.message = ACCOUNT_DEREGISTER_SUCCESSFUL;
    }

    public MerchantDeregisterResponse(String msg) {
        this.message = msg;
    }
}
