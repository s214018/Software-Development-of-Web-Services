package responses;

import lombok.Getter;
import lombok.Setter;
import messaging.domain.GenericResponse;
import requests.PaymentRequest;

import static messaging.domain.EventMessages.PAYMENT_SUCCESSFUL;

@Getter @Setter
public class PaymentResponse extends GenericResponse {
    private PaymentRequest paymentRequest;

    public PaymentResponse(){
        this.message = PAYMENT_SUCCESSFUL;
    }

    public PaymentResponse(String msg) {
        this.message = msg;
    }
}
