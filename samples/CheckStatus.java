package samples;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ravenbill.vanilla.ApiClient;
import com.ravenbill.vanilla.Config;

public class CheckStatus {

    public static void main(String[] args) throws InterruptedException {
        ApiClient api = ApiClient.login();
        String accountId = Config.ACCOUNT_ID;

        // 1. List all envelopes
        JsonElement all = api.get(
                "/api/accounts/" + accountId + "/envelopes"
        );
        System.out.println("All envelopes:");
        System.out.println(ApiClient.pretty(all));

        // 2. Filter by status — list only sent envelopes
        JsonElement sent = api.get(
                "/api/accounts/" + accountId + "/envelopes?status=sent"
        );
        System.out.println("\nSent envelopes:");
        System.out.println(ApiClient.pretty(sent));

        // 3. Get a specific envelope by ID (use the first one from the list)
        var items = all.getAsJsonObject().getAsJsonArray("data");
        if (items == null || items.isEmpty()) {
            System.out.println("No envelopes found.");
            return;
        }

        String envelopeId = items.get(0).getAsJsonObject()
                .get("id").getAsString();
        System.out.println("\nChecking envelope: " + envelopeId);

        JsonElement detail = api.get(
                "/api/accounts/" + accountId + "/envelopes/" + envelopeId
        );
        String status = detail.getAsJsonObject()
                .get("data").getAsJsonObject()
                .get("status").getAsString();
        System.out.println("Current status: " + status);

        // 4. Poll until completed (max 5 attempts, 3s interval)
        System.out.println("\nPolling for completion...");
        for (int i = 0; i < 5; i++) {
            JsonElement check = api.get(
                    "/api/accounts/" + accountId + "/envelopes/" + envelopeId
            );
            String currentStatus = check.getAsJsonObject()
                    .get("data").getAsJsonObject()
                    .get("status").getAsString();

            System.out.println("  Attempt " + (i + 1) + ": status = " + currentStatus);

            if ("completed".equals(currentStatus)) {
                System.out.println("Envelope is completed!");
                return;
            }

            Thread.sleep(3000);
        }

        System.out.println("Envelope not yet completed after polling.");
    }
}
