/**
 *
 * @author Julia Makulec
 *
 */
package responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CustomerResponse {
    private List<CustomerPaymentResponse> paymentResponseList;
}
