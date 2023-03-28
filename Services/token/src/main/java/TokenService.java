import domain.CorrelationId;
import domain.Token;
import lombok.NoArgsConstructor;
import messaging.Event;
import messaging.MessageQueue;
import responses.TokenActionResponse;
import responses.TokensResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static messaging.domain.EventTypeResource.*;

@NoArgsConstructor
/**
 *
 * @author Mads Klynderud (s215955)
 *
 */
public class TokenService {

   private static final HashMap<UUID, ArrayList<Token>> tokenMap = new HashMap<>();

   public TokensResponse createTokens(UUID customerID, int numTokens) {
       tokenMap.putIfAbsent(customerID, new ArrayList<>());

       if (tokenMap.get(customerID).size() > 1)
           return new TokensResponse("Customer has more than 1 token");

       if (numTokens < 1 || numTokens > 5)
           return new TokensResponse("Number of generated tokens cannot be less than 1 or more than 5");

       for (int i = 0; i < numTokens; i++) {
           Token newToken = new Token(UUID.randomUUID());
           tokenMap.get(customerID).add(newToken);
       }

       return new TokensResponse(customerID, tokenMap.get(customerID));
   }

   public TokenActionResponse consumeToken(UUID tokenID) {
       for (UUID customerID : tokenMap.keySet()) {
           for (Token token : tokenMap.get(customerID)) {
               if (token.getTokenID().equals(tokenID)) {
                   tokenMap.get(customerID).remove(token);
                   return new TokenActionResponse(tokenID);
               }
           }
       }
       return new TokenActionResponse("Token not found for customer");
   }

    public TokenActionResponse verifyToken(UUID tokenID) {
        for (UUID customerID : tokenMap.keySet()) {
            for (Token token : tokenMap.get(customerID)) {
                if (token.getTokenID().equals(tokenID))
                    return new TokenActionResponse(customerID);
            }
        }
        return new TokenActionResponse("Token not found");
    }


    MessageQueue queue;
    public TokenService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler(TOKEN_GENERATION_REQUEST, this::handleTokenGenerationRequest);
        this.queue.addHandler(TOKEN_CONSUME_REQUEST, this::handleTokenConsumeRequest);
        this.queue.addHandler(TOKEN_VERIFY_REQUEST, this::handleTokenVerifyRequest);
    }

    private void handleTokenGenerationRequest(Event ev) {
        var customerID = ev.getArgument(0, UUID.class);
        var numTokens = ev.getArgument(1, Integer.class);
        var correlationID = ev.getArgument(2, CorrelationId.class);
        TokensResponse response = createTokens(customerID, numTokens);
        Event event = new Event(TOKEN_GENERATION_RESPONSE, new Object[] { response, correlationID });
        queue.publish(event);
    }

    private void handleTokenConsumeRequest(Event ev) {
        System.out.println("token consume event recieved");
        var tokenID = ev.getArgument(0, UUID.class);
        var correlationID = ev.getArgument(1, CorrelationId.class);
        TokenActionResponse res = consumeToken(tokenID);
        Event event = new Event(TOKEN_CONSUME_FINISHED, new Object[] { res, correlationID });
        queue.publish(event);
    }

    private void handleTokenVerifyRequest(Event ev) {
        System.out.println("token verify event recieved");
        var tokenID = ev.getArgument(0, UUID.class);
        var correlationID = ev.getArgument(1, CorrelationId.class);
        TokenActionResponse response = verifyToken(tokenID);
        Event event = new Event(TOKEN_VERIFY_FINISHED, new Object[] { response, correlationID });
        queue.publish(event);
    }
}
