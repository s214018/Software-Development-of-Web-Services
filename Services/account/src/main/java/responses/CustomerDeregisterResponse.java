package responses;

import messaging.domain.GenericResponse;

import static messaging.domain.EventMessages.ACCOUNT_DEREGISTER_SUCCESSFUL;

public class CustomerDeregisterResponse extends GenericResponse {
    public CustomerDeregisterResponse(String msg) {
        this.message = msg;
    }
    public CustomerDeregisterResponse() {
        this.message = ACCOUNT_DEREGISTER_SUCCESSFUL;
    }
}
