// PlaidConfig.java
package config;

import io.github.cdimascio.dotenv.Dotenv;

public class PlaidConfig {
    private static final Dotenv dotenv = Dotenv.configure()
            .load();

    public static final String CLIENT_ID = dotenv.get("PLAID_CLIENT_ID");
    public static final String SECRET = dotenv.get("PLAID_SECRET");
    public static final String BASE_URL = "https://sandbox.plaid.com";
}