package com.ravenbill.vanilla;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class ApiClient {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    private final String token;

    public ApiClient(String token) {
        this.token = token;
    }

    public static ApiClient login() {
        return new ApiClient(AuthClient.signIn());
    }

    public JsonElement get(String path) {
        return request("GET", path, null);
    }

    public JsonElement post(String path, Object body) {
        return request("POST", path, body);
    }

    public JsonElement patch(String path, Object body) {
        return request("PATCH", path, body);
    }

    public JsonElement delete(String path) {
        return request("DELETE", path, null);
    }

    public byte[] getBytes(String path) {
        try {
            HttpRequest req = buildRequest("GET", path, null);
            HttpResponse<byte[]> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofByteArray());
            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                throw new RuntimeException("HTTP " + resp.statusCode() + " on GET " + path);
            }
            return resp.body();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Request failed: GET " + path, e);
        }
    }

    public JsonElement graphql(String query, JsonObject variables) {
        JsonObject body = new JsonObject();
        body.addProperty("query", query);
        if (variables != null) {
            body.add("variables", variables);
        }
        return request("POST", "/gql", body);
    }

    private JsonElement request(String method, String path, Object body) {
        try {
            HttpRequest req = buildRequest(method, path, body);
            HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                throw new RuntimeException("HTTP " + resp.statusCode() + " on " + method + " " + path + ": " + resp.body());
            }
            String respBody = resp.body();
            if (respBody == null || respBody.isBlank()) return null;
            return GSON.fromJson(respBody, JsonElement.class);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Request failed: " + method + " " + path, e);
        }
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        String url = Config.API_URL + path;
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        if (body != null) {
            String json = (body instanceof String) ? (String) body : GSON.toJson(body);
            builder.method(method, HttpRequest.BodyPublishers.ofString(json));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }
        return builder.build();
    }

    public static String pretty(JsonElement json) {
        return GSON.toJson(json);
    }
}
