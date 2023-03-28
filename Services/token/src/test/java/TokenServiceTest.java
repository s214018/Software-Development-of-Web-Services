import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import responses.TokenActionResponse;
import responses.TokensResponse;

import java.util.UUID;

import static org.junit.Assert.*;
/**
 *
 * @author Mads Klynderud - s215955
 * @author Gustav Nilsson Pedersen - s174562
 * @author Hassan Kassem - s205409
 *
 */
public class TokenServiceTest {

    private static final TokenService service = new TokenService();

    @Test
    @DisplayName("Token should be created")
    public void testCreateTokens() {
        // Test creating tokens for a new customer
        UUID customerID = UUID.randomUUID();
        TokensResponse response = service.createTokens(customerID, 3);
        assertEquals(customerID, response.getCustomerID());
        assertEquals(3, response.getTokens().size());
    }

    @Test
    @DisplayName("Token should not be created because current token is above 1")
    public void testTokenListAboveOne() {
        // Test creating tokens for a new customer
        UUID customerID = UUID.randomUUID();
        TokensResponse response = service.createTokens(customerID, 3);
        assertEquals(customerID, response.getCustomerID());
        assertEquals(3, response.getTokens().size());

        // Test creating tokens for an existing customer
        response = service.createTokens(customerID, 2);
        assertEquals("Customer has more than 1 token", response.getMessage());
    }

    @Test
    @DisplayName("Token should not be created for invalid token")
    public void testInvalidTokenGeneration() {
        // Test creating tokens for a new customer
        UUID customerID = UUID.randomUUID();
        TokensResponse response;

        // Test creating tokens with an invalid number
        response = service.createTokens(customerID, 0);
        assertEquals("Number of generated tokens cannot be less than 1 or more than 5", response.getMessage());
    }

    @Test
    @DisplayName("Token should be consumed")
    public void testConsumeToken() {
        // Test consuming a valid token
        UUID customerID = UUID.randomUUID();
        TokensResponse tokengen = service.createTokens(customerID, 1);
        assertEquals(1, tokengen.getTokens().size());
        UUID tokenID = tokengen.getTokens().get(0).getTokenID();
        TokenActionResponse response = service.consumeToken(tokenID);
        assertEquals(tokenID, response.getUuid());

        // Test consuming an invalid token
        response = service.consumeToken(UUID.randomUUID());
        assertEquals("Token not found for customer", response.getMessage());

        // Check the token list is empty
        assertTrue(tokengen.getTokens().isEmpty());
    }

    @Test
    @DisplayName("Token should be verified")
    public void testVerifyToken() {
        // Test verifying a valid token
        UUID customerID = UUID.randomUUID();
        TokensResponse tokengen = service.createTokens(customerID, 1);
        UUID tokenID = tokengen.getTokens().get(0).getTokenID();
        TokenActionResponse response = service.verifyToken(tokenID);
        assertEquals(customerID, response.getUuid());
    }

    @Test
    @DisplayName("Token should not be verified")
    public void testVerifyInvalidToken() {
        // Test verifying an invalid token
        TokenActionResponse response = service.verifyToken(UUID.randomUUID());
        assertEquals("Token not found", response.getMessage());
    }
}