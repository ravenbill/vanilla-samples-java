package samples;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ravenbill.vanilla.ApiClient;
import com.ravenbill.vanilla.Config;

public class UseTemplates {

    public static void main(String[] args) {
        ApiClient api = ApiClient.login();
        String accountId = Config.ACCOUNT_ID;

        // 1. List available templates
        JsonElement templates = api.get(
                "/api/accounts/" + accountId + "/templates"
        );
        System.out.println("Available templates:");
        System.out.println(ApiClient.pretty(templates));

        JsonArray items = templates.getAsJsonObject()
                .getAsJsonArray("data");

        if (items == null || items.isEmpty()) {
            System.out.println("No templates found. Create one in the UI first.");
            return;
        }

        // 2. Pick the first template
        String templateId = items.get(0).getAsJsonObject()
                .get("id").getAsString();
        String templateName = items.get(0).getAsJsonObject()
                .get("title").getAsString();
        System.out.println("\nUsing template: " + templateName + " (" + templateId + ")");

        // 3. Create an envelope from the template
        JsonObject envelope = new JsonObject();
        envelope.addProperty("template_id", templateId);
        envelope.addProperty("title", "From Template: " + templateName);
        envelope.addProperty("message", "Created from a template via the Java SDK.");

        JsonElement created = api.post(
                "/api/accounts/" + accountId + "/envelopes",
                envelope
        );
        String envelopeId = created.getAsJsonObject()
                .get("data").getAsJsonObject()
                .get("id").getAsString();
        System.out.println("Created envelope from template: " + envelopeId);

        // 4. Override template recipient with actual signer
        JsonObject recipient = new JsonObject();
        recipient.addProperty("email", "real-signer@example.com");
        recipient.addProperty("name", "Real Signer");
        recipient.addProperty("role", "signer");

        api.post(
                "/api/accounts/" + accountId + "/envelopes/" + envelopeId + "/recipients",
                recipient
        );
        System.out.println("Added recipient to template envelope");

        // 5. Send
        JsonObject sendAction = new JsonObject();
        sendAction.addProperty("status", "sent");

        api.patch("/api/accounts/" + accountId + "/envelopes/" + envelopeId, sendAction);
        System.out.println("Envelope sent!");
    }
}
