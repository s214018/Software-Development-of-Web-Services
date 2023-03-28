package requests;

/**
 *
 * @author Hassan Kassem | git: stonebank | student: s205409
 *
 *
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequest {
    private UUID customerID;
    private int numberOfTokens;
}
