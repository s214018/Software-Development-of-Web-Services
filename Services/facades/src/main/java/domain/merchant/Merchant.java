package domain.merchant;

import domain.Account;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor

public class Merchant extends Account {
    public Merchant(String bankAccountID, UUID randomUUID) {
        super(randomUUID, bankAccountID);
    }
}
