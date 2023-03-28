package domain.customer;

import domain.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokensResponse {
    private String message;
    private UUID customerID;
    private List<Token> tokens;
}
