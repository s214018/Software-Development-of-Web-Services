package domain.customer;

import lombok.Getter;
import messaging.domain.GenericResponse;


@Getter
public class CustomerCreationResponse extends GenericResponse {
    private Customer customer;
}