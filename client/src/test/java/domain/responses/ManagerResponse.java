/**
 *
 * @author Hjalte Bøgehave
 *
 */
package domain.responses;

import domain.ManagerPayment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class ManagerResponse {
    private double totalPaymentAmount;
    private List<ManagerPayment> paymentList;
}
