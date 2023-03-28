package domain.merchant;

import lombok.Getter;
import messaging.domain.GenericResponse;

@Getter
public class MerchantCreationResponse extends GenericResponse {
    private Merchant merchant;
}