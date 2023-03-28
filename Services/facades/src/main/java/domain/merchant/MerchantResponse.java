/**
 *
 * @author Hjalte Bøgehave
 *
 */
package domain.merchant;

import domain.Payment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
public class MerchantResponse {
    private UUID token;
    private double amount;

    public MerchantResponse(Payment payment) {
        token = payment.getToken();
        amount = payment.getAmount();
    }
}
