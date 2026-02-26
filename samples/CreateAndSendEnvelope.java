package samples;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ravenbill.vanilla.ApiClient;
import com.ravenbill.vanilla.Config;

public class CreateAndSendEnvelope {

    public static void main(String[] args) {
        ApiClient api = ApiClient.login();
        String accountId = Config.ACCOUNT_ID;

        // 1. Create a draft envelope
        JsonObject envelope = new JsonObject();
        envelope.addProperty("title", "Sample Envelope from Java");
        envelope.addProperty("message", "Please review and sign this document.");

        JsonElement created = api.post(
                "/api/accounts/" + accountId + "/envelopes",
                envelope
        );

        String envelopeId = created.getAsJsonObject()
                .get("data").getAsJsonObject()
                .get("id").getAsString();
        System.out.println("Created draft envelope: " + envelopeId);

        // 2. Add a recipient
        JsonObject recipient = new JsonObject();
        recipient.addProperty("email", "signer@example.com");
        recipient.addProperty("name", "Jane Signer");
        recipient.addProperty("role", "signer");

        api.post(
                "/api/accounts/" + accountId + "/envelopes/" + envelopeId + "/recipients",
                recipient
        );
        System.out.println("Added recipient: signer@example.com");

        // 3. Send the envelope
        JsonObject sendAction = new JsonObject();
        sendAction.addProperty("status", "sent");

        JsonElement sent = api.patch(
                "/api/accounts/" + accountId + "/envelopes/" + envelopeId,
                sendAction
        );
        System.out.println("Envelope sent!");
        System.out.println(ApiClient.pretty(sent));
    }
}
