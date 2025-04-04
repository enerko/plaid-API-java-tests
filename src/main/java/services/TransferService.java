package services;

import config.PlaidConfig;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;

public class TransferService {
    public static String createTransferAuthId(String accessToken, String accountId, String amount) {
        Response transferAuthResponse = getTransferAuthResponse(accessToken, accountId, amount);
        return transferAuthResponse.jsonPath().getString("authorization.id");
    }

    public static Response getTransferAuthResponse(String accessToken, String accountId, String amount) {
        // Create request body
        JSONObject transferAuthRequest = new JSONObject();
        transferAuthRequest.put("client_id", PlaidConfig.CLIENT_ID);
        transferAuthRequest.put("secret", PlaidConfig.SECRET);
        transferAuthRequest.put("access_token", accessToken);
        transferAuthRequest.put("account_id", accountId);
        transferAuthRequest.put("type", "debit");
        transferAuthRequest.put("network", "ach");
        transferAuthRequest.put("ach_class", "ppd");
        transferAuthRequest.put("amount", amount);
        transferAuthRequest.put("user", new JSONObject().put("legal_name", "John Doe"));

        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(transferAuthRequest.toString())
                .post(PlaidConfig.BASE_URL + "/transfer/authorization/create");
    }

    public static String createTransferId(String accessToken, String accountId, String authId) {

        // Create request body
        JSONObject transferRequest = new JSONObject();
        transferRequest.put("client_id", PlaidConfig.CLIENT_ID);
        transferRequest.put("secret", PlaidConfig.SECRET);
        transferRequest.put("access_token", accessToken);
        transferRequest.put("account_id", accountId);
        transferRequest.put("authorization_id", authId);
        transferRequest.put("description", "Payment");

        Response transferResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(transferRequest.toString())
                .post(PlaidConfig.BASE_URL + "/transfer/create");

        return transferResponse.jsonPath().getString("transfer.id");
    }

    public static Response simulateTransfer(JSONObject transferRequest) {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(transferRequest.toString())
                .post( PlaidConfig.BASE_URL + "/sandbox/transfer/simulate");
    }
}
