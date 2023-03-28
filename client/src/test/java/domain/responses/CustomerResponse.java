package domain.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter @Setter
public class CustomerResponse {
    private List<CustomerPaymentResponse> paymentResponseList;
}

