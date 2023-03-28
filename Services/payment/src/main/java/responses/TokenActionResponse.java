package responses;

import lombok.Getter;
import lombok.Setter;
import messaging.domain.GenericResponse;

import java.util.UUID;

@Getter @Setter
public class TokenActionResponse extends GenericResponse {
    private UUID uuid;
}
