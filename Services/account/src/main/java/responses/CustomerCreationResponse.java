package responses;

import domain.Customer;
import lombok.Getter;
import messaging.domain.GenericResponse;

import static messaging.domain.EventMessages.ACCOUNT_GENERATION_SUCCESSFUL;


@Getter
public class CustomerCreationResponse extends GenericResponse {
    private Customer customer;

    public CustomerCreationResponse(Customer customer) {
        this.customer = customer;
        this.message = ACCOUNT_GENERATION_SUCCESSFUL;
    }

    public CustomerCreationResponse(String msg) {
        this.message = msg;
    }
}