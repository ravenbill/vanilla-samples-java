package samples;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ravenbill.vanilla.ApiClient;
import com.ravenbill.vanilla.Config;

public class GraphqlQueries {

    public static void main(String[] args) {
        ApiClient api = ApiClient.login();
        String accountId = Config.ACCOUNT_ID;

        // 1. Query — list envelopes with GraphQL
        System.out.println("=== Query: List Envelopes ===");
        String listQuery = """
                query ListEnvelopes($accountId: ID!, $limit: Int) {
                  envelopes(accountId: $accountId, limit: $limit) {
                    id
                    title
                    status
                    createdAt
                    recipients {
                      name
                      email
                      status
                    }
                  }
                }
                """;

        JsonObject listVars = new JsonObject();
        listVars.addProperty("accountId", accountId);
        listVars.addProperty("limit", 5);

        JsonElement listResult = api.graphql(listQuery, listVars);
        System.out.println(ApiClient.pretty(listResult));

        // 2. Query — get account details
        System.out.println("\n=== Query: Account Details ===");
        String accountQuery = """
                query GetAccount($accountId: ID!) {
                  account(id: $accountId) {
                    id
                    name
                    plan
                    usage {
                      envelopesSent
                      envelopesRemaining
                    }
                  }
                }
                """;

        JsonObject accountVars = new JsonObject();
        accountVars.addProperty("accountId", accountId);

        JsonElement accountResult = api.graphql(accountQuery, accountVars);
        System.out.println(ApiClient.pretty(accountResult));

        // 3. Mutation — create an envelope via GraphQL
        System.out.println("\n=== Mutation: Create Envelope ===");
        String createMutation = """
                mutation CreateEnvelope($input: CreateEnvelopeInput!) {
                  createEnvelope(input: $input) {
                    envelope {
                      id
                      title
                      status
                    }
                    errors {
                      field
                      message
                    }
                  }
                }
                """;

        JsonObject input = new JsonObject();
        input.addProperty("accountId", accountId);
        input.addProperty("title", "GraphQL-Created Envelope");
        input.addProperty("message", "Created via a GraphQL mutation.");

        JsonObject mutationVars = new JsonObject();
        mutationVars.add("input", input);

        JsonElement mutationResult = api.graphql(createMutation, mutationVars);
        System.out.println(ApiClient.pretty(mutationResult));
    }
}
