package api;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.AccountService;
import services.TokenService;

import static org.junit.jupiter.api.Assertions.*;

public class BalanceTests {

    private String accessToken;

    @BeforeEach
    public void setup() {
        String publicToken = TokenService.getPublicToken();
        accessToken = TokenService.getAccessToken(publicToken);
    }

    @Test
    public void testBalanceValid() {
        Response response = AccountService.getBalances(accessToken);

        assertEquals(200, response.getStatusCode());
        assertNotNull(response.jsonPath().get("accounts[0].balances.available"));
        assertNotNull(response.jsonPath().get("accounts[0].balances.current"));
        assertNotNull(response.jsonPath().get("accounts[0].balances.iso_currency_code"));
    }
}
