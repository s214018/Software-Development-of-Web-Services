/**
 *
 * @author Hjalte Bøgehave
 *
 */
package domain.manager;

import domain.Payment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ManagerPayment {
    private double amount;
    private UUID customerID;
    private UUID merchantID;

    public ManagerPayment(Payment payment) {
        amount = payment.getAmount();
        customerID = payment.getCustomerID();
        merchantID = payment.getMerchantID();
    }
}
