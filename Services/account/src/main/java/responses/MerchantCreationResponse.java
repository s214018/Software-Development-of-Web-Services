package responses;

import domain.Merchant;
import lombok.Getter;
import messaging.domain.GenericResponse;


import static messaging.domain.EventMessages.ACCOUNT_GENERATION_SUCCESSFUL;

@Getter
public class MerchantCreationResponse extends GenericResponse {
    private Merchant merchant;

    public MerchantCreationResponse(Merchant merchant) {
        this.merchant = merchant;
        this.message = ACCOUNT_GENERATION_SUCCESSFUL;
    }

    public MerchantCreationResponse(String msg) {
        this.message = msg;
    }
}