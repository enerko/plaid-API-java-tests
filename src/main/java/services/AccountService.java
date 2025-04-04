package services;

import config.PlaidConfig;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;

public class AccountService {
    public static String getAccountId(String accessToken) {
        // Create request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("client_id", PlaidConfig.CLIENT_ID);
        requestBody.put("secret", PlaidConfig.SECRET);
        requestBody.put("access_token", accessToken);

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .post(PlaidConfig.BASE_URL + "/accounts/get");

        return response.jsonPath().getString("accounts[0].account_id");
    }

    public static Response getBalances(String accessToken) {
        // Create request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("client_id", PlaidConfig.CLIENT_ID);
        requestBody.put("secret", PlaidConfig.SECRET);
        requestBody.put("access_token", accessToken);

        // Send request
        return RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .post(PlaidConfig.BASE_URL + "/accounts/balance/get");
    }
}
