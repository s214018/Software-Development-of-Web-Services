/**
 *
 * @author Hjalte Bøgehave
 *
 */
package domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class ManagerPayment {
    private double amount;
    private UUID customerID;
    private UUID merchantID;
}
