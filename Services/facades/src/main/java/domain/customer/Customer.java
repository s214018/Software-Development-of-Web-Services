package domain.customer;

import domain.Account;
import domain.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Customer extends Account {

    private ArrayList<Token> tokens = new ArrayList<>();

    public Customer(String bankAccountID, UUID randomUUID) {
        super(randomUUID, bankAccountID);
    }
}
