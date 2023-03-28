package domain.responses;


import domain.Merchant;
import domain.messages.GenericResponse;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MerchantCreationResponse extends GenericResponse {
    private Merchant merchant;
}