package responses;

import domain.Customer;
import lombok.Getter;
import messaging.domain.GenericResponse;

import static messaging.domain.EventMessages.ACCOUNT_RETRIEVAL_SUCCESSFUL;

@Getter
public class CustomerRetrieveResponse extends GenericResponse {

        private Customer customer;

        public CustomerRetrieveResponse(Customer customer) {
            this.customer = customer;
            this.message = ACCOUNT_RETRIEVAL_SUCCESSFUL;
        }

        public CustomerRetrieveResponse(String msg) {
            this.message = msg;
        }
}
