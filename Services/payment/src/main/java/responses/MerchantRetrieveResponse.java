package responses;

import domain.Merchant;
import lombok.Getter;
import messaging.domain.GenericResponse;


@Getter
public class MerchantRetrieveResponse extends GenericResponse {
    private Merchant merchant;
}
