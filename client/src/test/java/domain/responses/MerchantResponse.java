/**
 *
 * @author Hjalte Bøgehave
 *
 */
package domain.responses;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class MerchantResponse {
    private UUID token;
    private double amount;
}
