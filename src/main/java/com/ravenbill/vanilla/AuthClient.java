package com.ravenbill.vanilla;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class AuthClient {

    private static final Gson GSON = new Gson();
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    private AuthClient() {}

    /**
     * Authenticate with email + password and return a JWT bearer token.
     */
    public static String signIn() {
        return signIn(Config.EMAIL, Config.PASSWORD);
    }

    public static String signIn(String email, String password) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("email", email);
            body.addProperty("password", password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Config.API_URL + "/api/auth/sign-in"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(body)))
                    .build();

            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Sign-in failed (" + response.statusCode() + "): " + response.body());
            }

            JsonObject json = GSON.fromJson(response.body(), JsonObject.class);
            return json.get("token").getAsString();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Sign-in request failed", e);
        }
    }
}
