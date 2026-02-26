package samples;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ravenbill.vanilla.ApiClient;
import com.ravenbill.vanilla.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// NOTE: Bulk Send feature is not yet implemented in Vanilla Esign.
// This sample demonstrates the planned API design and may change.

public class BulkSend {

    public static void main(String[] args) throws IOException {
        ApiClient api = ApiClient.login();
        String accountId = Config.ACCOUNT_ID;
        Path csvPath = Path.of("data/recipients.csv");

        if (!Files.exists(csvPath)) {
            System.out.println("CSV file not found: " + csvPath.toAbsolutePath());
            return;
        }

        System.out.println("Reading recipients from " + csvPath);
        int count = 0;

        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String header = reader.readLine(); // skip header
            if (header == null) {
                System.out.println("CSV is empty.");
                return;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 3);
                if (parts.length < 2) {
                    System.out.println("Skipping malformed row: " + line);
                    continue;
                }

                String name = parts[0].trim();
                String email = parts[1].trim();
                String role = parts.length > 2 ? parts[2].trim() : "signer";

                // Create envelope
                JsonObject envelope = new JsonObject();
                envelope.addProperty("title", "Bulk Envelope for " + name);
                envelope.addProperty("message", "Please sign this document.");

                JsonElement created = api.post(
                        "/api/accounts/" + accountId + "/envelopes",
                        envelope
                );
                String envelopeId = created.getAsJsonObject()
                        .get("data").getAsJsonObject()
                        .get("id").getAsString();

                // Add recipient
                JsonObject recipient = new JsonObject();
                recipient.addProperty("name", name);
                recipient.addProperty("email", email);
                recipient.addProperty("role", role);

                api.post(
                        "/api/accounts/" + accountId + "/envelopes/" + envelopeId + "/recipients",
                        recipient
                );

                // Send
                JsonObject sendAction = new JsonObject();
                sendAction.addProperty("status", "sent");
                api.patch("/api/accounts/" + accountId + "/envelopes/" + envelopeId, sendAction);

                count++;
                System.out.println("  Sent envelope " + envelopeId + " to " + name + " <" + email + ">");
            }
        }

        System.out.println("\nDone! Sent " + count + " envelopes.");
    }
}
