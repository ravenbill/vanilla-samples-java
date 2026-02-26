package samples;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public class WebhookHandler {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int PORT = 8080;
    private static final String WEBHOOK_SECRET = System.getenv("VANILLA_WEBHOOK_SECRET") != null
            ? System.getenv("VANILLA_WEBHOOK_SECRET")
            : "your-webhook-secret";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/webhook", WebhookHandler::handleWebhook);

        server.setExecutor(null);
        server.start();
        System.out.println("Webhook listener started on http://localhost:" + PORT + "/webhook");
        System.out.println("Press Ctrl+C to stop.");
    }

    private static void handleWebhook(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            respond(exchange, 405, "Method not allowed");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String signature = exchange.getRequestHeaders().getFirst("X-Vanilla-Signature");

        System.out.println("\n--- Incoming webhook ---");

        // Verify HMAC signature if present
        if (signature != null && !signature.isBlank()) {
            String computed = hmacSha256(WEBHOOK_SECRET, body);
            if (signature.equals(computed)) {
                System.out.println("Signature: VALID");
            } else {
                System.out.println("Signature: INVALID (expected " + computed + ", got " + signature + ")");
                respond(exchange, 401, "Invalid signature");
                return;
            }
        } else {
            System.out.println("Signature: not present (skipping verification)");
        }

        // Parse and display the event
        JsonObject event = GSON.fromJson(body, JsonObject.class);
        String eventType = event.has("event") ? event.get("event").getAsString() : "unknown";
        System.out.println("Event type: " + eventType);
        System.out.println("Payload:\n" + GSON.toJson(event));

        respond(exchange, 200, "{\"received\": true}");
    }

    private static String hmacSha256(String secret, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("HMAC computation failed", e);
        }
    }

    private static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
