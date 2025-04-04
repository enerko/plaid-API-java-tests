package services;

import config.PlaidConfig;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;

public class AuthService {
    public static Response getAuthData(String accessToken) {
        // Create request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("client_id", PlaidConfig.CLIENT_ID);
        requestBody.put("secret", PlaidConfig.SECRET);
        requestBody.put("access_token", accessToken);

        // Send request
        return getAuthResponse(requestBody);
    }

    public static Response getAuthResponse(JSONObject requestBody) {
        return RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .post(PlaidConfig.BASE_URL + "/auth/get");
    }
}
