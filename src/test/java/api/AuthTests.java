package api;

import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import services.AuthService;
import services.TokenService;


public class AuthTests {

    private String accessToken;

    @BeforeEach
    public void setup() {
        String publicToken = TokenService.getPublicToken();
        accessToken = TokenService.getAccessToken(publicToken);
    }

    @Test
    public void testAuthValid() {
        Response authData = AuthService.getAuthData(accessToken);

        assertNotNull(authData, "Auth response should not be null");
        assertEquals(200, authData.getStatusCode(), "Expected status code 200");

        JSONObject jsonResponse = new JSONObject(authData.asString());
        assertTrue(jsonResponse.has("numbers"), "Response should contain 'numbers'");

        String accountNumber = jsonResponse.getJSONObject("numbers").getJSONArray("ach").getJSONObject(0).getString("account");
        assertNotNull(accountNumber, "ACH Account should not be null");
    }

    @Test
    public void testAuthInvalid() {
        // Get the last character and increment its ASCII value
        char lastChar = accessToken.charAt(accessToken.length() - 1);
        String corruptAccessToken = accessToken.substring(0, accessToken.length() - 1) + (char) (lastChar + 1);

        Response authData = AuthService.getAuthData(corruptAccessToken);

        assertNotNull(authData, "Auth response should not be null");
        assertEquals("INVALID_ACCESS_TOKEN", authData.jsonPath().getString("error_code"), "Expected error code INVALID_ACCESS_TOKEN");
    }

    @Test
    public void testAuthExpired() {
        // Invalidate the old access token
        String newAccessToken = TokenService.getNewAccessToken(accessToken);

        Response authData = AuthService.getAuthData(accessToken);

        assertEquals("ITEM_NOT_FOUND", authData.jsonPath().getString("error_code"), "Expected error code ITEM_NOT_FOUND");
    }

    @Test
    public void testWithoutAuthHeaders() {
        // Create request body without client_id or secret
        JSONObject requestBody = new JSONObject();
        requestBody.put("access_token", accessToken);

        Response response = AuthService.getAuthResponse(requestBody);

        assertEquals(400, response.getStatusCode(), "Expected status code 400");

        // Verify that Plaid returns an authentication error
        assertEquals("MISSING_FIELDS", response.jsonPath().getString("error_code"), "Expected error code MISSING_FIELDS");
    }

    @Test
    public void testSQLInjectionInClientId() {
        // Inject SQL
        JSONObject requestBody = new JSONObject();
        requestBody.put("client_id", "' OR '1'='1");
        requestBody.put("secret", "fake_secret");
        requestBody.put("access_token", accessToken);

        Response response = AuthService.getAuthResponse(requestBody);

        assertEquals(400, response.getStatusCode(), "Expected status code 400");

        // Verify that response does not expose SQL errors
        assertFalse(response.asString().contains("SQL"), "Response should not expose SQL errors");
        assertEquals("INVALID_FIELD", response.jsonPath().getString("error_code"), "Expected error code INVALID_REQUEST");
    }
}
