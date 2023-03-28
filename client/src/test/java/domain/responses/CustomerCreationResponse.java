package domain.responses;
import domain.Customer;
import domain.messages.GenericResponse;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomerCreationResponse extends GenericResponse {
    private Customer customer;
}