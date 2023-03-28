import org.junit.jupiter.api.Test;
import responses.CustomerCreationResponse;
import responses.CustomerRetrieveResponse;
import responses.MerchantCreationResponse;

import java.util.UUID;

import static messaging.domain.EventMessages.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
/**
 *
 * @author Muaz Ahmed
 *
 */

public class AccountServiceTest {
    private static AccountService service = new AccountService();

    @Test
    public void testCreateCustomer() {
        // Test creating a customer with a valid bank account ID
        String bankAccountID = "1234567890";
        CustomerCreationResponse response = service.createCustomer(bankAccountID);
        assertNotNull(response.getCustomer());
        assertEquals(bankAccountID, response.getCustomer().getBankAccountID());
    }

    @Test
    public void testCreateInvalidCustomer() {
        // Test creating a customer with an invalid bank account ID
        CustomerCreationResponse response = service.createCustomer(null);
        assertEquals(NO_BANK_ACC_PROVIDED, response.getMessage());
    }

    @Test
    public void testGetCustomer() {
        // Test getting a valid customer
        String bankID = "1234567890";
        CustomerCreationResponse customer = service.createCustomer(bankID);
        assertNotNull(customer.getCustomer());
        assertEquals(bankID, customer.getCustomer().getBankAccountID());

        // Test getting an invalid customer
        CustomerRetrieveResponse customer1 = service.getCustomer(UUID.randomUUID());
        assertEquals(CUSTOMER_RETRIEVAL_ERROR, customer1.getMessage());
    }

    @Test
    public void TestGetInvalidCustomer() {
        // Test getting an invalid customer
        CustomerRetrieveResponse customer1 = service.getCustomer(UUID.randomUUID());
        assertEquals(CUSTOMER_RETRIEVAL_ERROR, customer1.getMessage());
    }

    @Test
    public void testCreateMerchant() {
        // Test creating a merchant with a valid bank account ID
        String bankAccountID = "0987654321";
        MerchantCreationResponse response = service.createMerchant(bankAccountID);
        assertNotNull(response.getMerchant());
        assertEquals(bankAccountID, response.getMerchant().getBankAccountID());

        // Test creating a merchant with an invalid bank account ID
        response = service.createMerchant(null);
        assertEquals(NO_BANK_ACC_PROVIDED, response.getMessage());
    }

    @Test
    public void testGetMerchant() {
        // Test getting a valid merchant
        String bankAccountID = "0987654321";
        MerchantCreationResponse response = service.createMerchant(bankAccountID);
        assertNotNull(response.getMerchant());
        assertEquals(bankAccountID, response.getMerchant().getBankAccountID());
    }

    @Test
    public void testGetInvalidMerchant() {
        // Test creating a merchant with an invalid bank account ID
        MerchantCreationResponse response = service.createMerchant(null);
        assertEquals(NO_BANK_ACC_PROVIDED, response.getMessage());
    }
}
