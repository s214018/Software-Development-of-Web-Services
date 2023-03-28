/**
 *
 * @author Hjalte Bøgehave
 *
 */
package domain.manager;

import domain.Payment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ManagerResponse {
    private double totalPaymentAmount;
    private List<ManagerPayment> paymentList;

    public ManagerResponse(ArrayList<Payment> payments) {
        paymentList = payments.stream().map(ManagerPayment::new).collect(Collectors.toList());
        totalPaymentAmount = payments.stream().mapToDouble(Payment::getAmount).sum();
    }
}
