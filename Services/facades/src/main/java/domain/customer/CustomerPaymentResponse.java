/**
 *
 * @author Hjalte Bøgehave
 *
 */
package domain.customer;

import domain.Payment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
public class CustomerPaymentResponse {
    private double amount;
    private UUID merchantID;
    private UUID token;

    public CustomerPaymentResponse(Payment payment) {
        amount = payment.getAmount();
        merchantID = payment.getMerchantID();
        token = payment.getToken();
    }
}
