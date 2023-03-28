package responses;

import domain.Merchant;
import lombok.Getter;
import messaging.domain.GenericResponse;

import static messaging.domain.EventMessages.ACCOUNT_RETRIEVAL_SUCCESSFUL;

@Getter
public class MerchantRetrieveResponse extends GenericResponse {
    private Merchant merchant;

    public MerchantRetrieveResponse(Merchant merchant) {
        this.merchant = merchant;
        this.message = ACCOUNT_RETRIEVAL_SUCCESSFUL;
    }

    public MerchantRetrieveResponse(String msg) {
        this.message = msg;
    }
}
