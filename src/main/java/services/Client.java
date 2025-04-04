package services;

import config.PlaidConfig;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;

public class Client {
    public static Response post(String endpoint, JSONObject requestBody) {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post(PlaidConfig.BASE_URL + endpoint);
    }

    public static JSONObject getRequestBody() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("client_id", PlaidConfig.CLIENT_ID);
        requestBody.put("secret", PlaidConfig.SECRET);

        return requestBody;
    }
}