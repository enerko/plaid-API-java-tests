package api;

import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import services.AccountService;
import services.Client;
import services.TokenService;
import services.TransferService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TransferTests {
    private String accessToken;
    private String accountId;

    @BeforeEach
    public void setup() {
        String publicToken = TokenService.getPublicToken();
        accessToken = TokenService.getAccessToken(publicToken);
        accountId = AccountService.getAccountId(accessToken);
    }

    @Test
    public void testTransferValid() {
        String transferAuthId = TransferService.createTransferAuthId(accessToken, accountId, "100.00");
        String transferId = TransferService.createTransferId(accessToken, accountId, transferAuthId);

        JSONObject transferRequest = Client.getRequestBody();
        transferRequest.put("transfer_id", transferId);
        transferRequest.put("event_type", "posted");

        Response response = TransferService.simulateTransfer(transferRequest);

        assertEquals(200, response.getStatusCode());
        assertNotNull(response.jsonPath().getString("request_id"), "Expected a request_id");
    }

    @Test
    public void testTransferInvalidId() {
        // Generate a random uuid for transfer id
        UUID uuid = UUID.randomUUID();

        JSONObject transferRequest = Client.getRequestBody();

        transferRequest.put("transfer_id", uuid);
        transferRequest.put("event_type", "posted");

        Response response = TransferService.simulateTransfer(transferRequest);

        assertEquals("NOT_FOUND", response.jsonPath().getString("error_code"), "Expected error code NOT_FOUND");
    }

    @Test
    public void testTransferNegative() {
        // Attempt to authorize a transfer with a negative amount
        Response transferAuthResponse = TransferService.getTransferAuthResponse(accessToken, accountId, "-100.00");

        assertEquals(400, transferAuthResponse.getStatusCode());
        assertEquals( "amount must be a decimal with 2 places greater than 0, such as 0.10", transferAuthResponse.jsonPath().getString("error_message"));
    }
}