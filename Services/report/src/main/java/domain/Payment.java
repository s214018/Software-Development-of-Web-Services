/**
 *
 * @author Julia Makulec
 *
 */
package domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private UUID customerID;
    private UUID merchantID;
    private UUID token;
    private double amount;

}
