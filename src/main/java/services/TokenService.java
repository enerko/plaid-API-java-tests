package services;

import config.PlaidConfig;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class TokenService {
    public static String getPublicToken() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("client_id", PlaidConfig.CLIENT_ID);
        requestBody.put("secret", PlaidConfig.SECRET);
        requestBody.put("institution_id", "ins_109508");
        requestBody.put("initial_products", new JSONArray().put("auth"));

        // Make a POST request to create public token
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .post(PlaidConfig.BASE_URL + "/sandbox/public_token/create");

        return response.jsonPath().getString("public_token");
    }

    public static String getAccessToken(String publicToken) {
        // Request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("client_id", "67e6e2b105d1170022ed9c65");
        requestBody.put("secret", "79d74b1e5e9926144225d187fb5dfa");
        requestBody.put("public_token", publicToken);

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .post(PlaidConfig.BASE_URL + "/item/public_token/exchange");

        return response.jsonPath().getString("access_token");
    }

    public static String getNewAccessToken(String accessToken) {

        // Create request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("client_id", PlaidConfig.CLIENT_ID);
        requestBody.put("secret", PlaidConfig.SECRET);
        requestBody.put("access_token", accessToken);

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .post(PlaidConfig.BASE_URL + "/item/access_token/invalidate");

        return response.jsonPath().getString("new_access_token");
    }
}
