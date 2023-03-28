/**
 *
 * @author Hjalte Bøgehave
 *
 */
package domain.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter @Setter
public class CustomerPaymentResponse {
    private double amount;
    private UUID merchantID;
    private UUID token;
}
