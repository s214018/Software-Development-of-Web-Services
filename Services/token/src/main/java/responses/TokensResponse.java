package responses;

import domain.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.UUID;

import static messaging.domain.EventMessages.TOKEN_GENERATION_SUCCESSFUL;

/**
 *
 * @author Hassan Kassem | git: stonebank | student: s205409
 *
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokensResponse {
    private String message;
    private UUID customerID;
    private ArrayList<Token> tokens = new ArrayList<>();

    public TokensResponse(UUID customer, ArrayList<Token> tokens) {
        this.customerID = customer;
        this.tokens = tokens;
        this.message = TOKEN_GENERATION_SUCCESSFUL;
    }

    public TokensResponse(String message) {
        this.message = message;
    }
}
