package responses;

/**
 *
 * @author Hassan Kassem | git: stonebank | student: s205409
 *
 *
 */

import lombok.*;
import messaging.domain.GenericResponse;

import java.util.UUID;

import static messaging.domain.EventMessages.*;
@Getter @Setter
public class TokenActionResponse extends GenericResponse {
    private UUID uuid;
    public TokenActionResponse(UUID uuid) {
        this.uuid = uuid;
        this.message = TOKEN_ACTI0N_SUCCESSFUL;
    }

    public TokenActionResponse(String message) {
        this.message = message;
    }
}
