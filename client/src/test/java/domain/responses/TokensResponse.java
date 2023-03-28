package domain.responses;

import domain.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokensResponse {
    private String message;
    private UUID customerID;
    private ArrayList<Token> tokens = new ArrayList<>();
}
