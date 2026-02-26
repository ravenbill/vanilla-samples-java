package samples;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ravenbill.vanilla.ApiClient;
import com.ravenbill.vanilla.Config;

public class AddRecipients {

    public static void main(String[] args) {
        ApiClient api = ApiClient.login();
        String accountId = Config.ACCOUNT_ID;

        // 1. Create a draft envelope
        JsonObject envelope = new JsonObject();
        envelope.addProperty("title", "Multi-Recipient Envelope");
        envelope.addProperty("message", "Multiple signers needed.");

        JsonElement created = api.post(
                "/api/accounts/" + accountId + "/envelopes",
                envelope
        );
        String envelopeId = created.getAsJsonObject()
                .get("data").getAsJsonObject()
                .get("id").getAsString();
        System.out.println("Created envelope: " + envelopeId);

        // 2. Add first recipient
        JsonObject signer1 = new JsonObject();
        signer1.addProperty("email", "alice@example.com");
        signer1.addProperty("name", "Alice");
        signer1.addProperty("role", "signer");
        signer1.addProperty("order", 1);

        JsonElement r1 = api.post(
                "/api/accounts/" + accountId + "/envelopes/" + envelopeId + "/recipients",
                signer1
        );
        String recipientId1 = r1.getAsJsonObject()
                .get("data").getAsJsonObject()
                .get("id").getAsString();
        System.out.println("Added recipient Alice: " + recipientId1);

        // 3. Add second recipient
        JsonObject signer2 = new JsonObject();
        signer2.addProperty("email", "bob@example.com");
        signer2.addProperty("name", "Bob");
        signer2.addProperty("role", "signer");
        signer2.addProperty("order", 2);

        JsonElement r2 = api.post(
                "/api/accounts/" + accountId + "/envelopes/" + envelopeId + "/recipients",
                signer2
        );
        String recipientId2 = r2.getAsJsonObject()
                .get("data").getAsJsonObject()
                .get("id").getAsString();
        System.out.println("Added recipient Bob: " + recipientId2);

        // 4. Add signature tabs for first recipient
        JsonObject tab = new JsonObject();
        tab.addProperty("type", "signature");
        tab.addProperty("page", 1);
        tab.addProperty("x", 200);
        tab.addProperty("y", 400);

        api.post(
                "/api/accounts/" + accountId + "/envelopes/" + envelopeId
                        + "/recipients/" + recipientId1 + "/tabs",
                tab
        );
        System.out.println("Added signature tab for Alice");

        // 5. Add date-signed tab for second recipient
        JsonObject dateTab = new JsonObject();
        dateTab.addProperty("type", "date_signed");
        dateTab.addProperty("page", 1);
        dateTab.addProperty("x", 200);
        dateTab.addProperty("y", 500);

        api.post(
                "/api/accounts/" + accountId + "/envelopes/" + envelopeId
                        + "/recipients/" + recipientId2 + "/tabs",
                dateTab
        );
        System.out.println("Added date_signed tab for Bob");

        // 6. List all recipients
        JsonElement recipients = api.get(
                "/api/accounts/" + accountId + "/envelopes/" + envelopeId + "/recipients"
        );
        System.out.println("\nAll recipients:");
        System.out.println(ApiClient.pretty(recipients));
    }
}
