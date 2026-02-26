package samples;

import com.google.gson.JsonElement;
import com.ravenbill.vanilla.ApiClient;
import com.ravenbill.vanilla.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DownloadDocuments {

    public static void main(String[] args) throws IOException {
        ApiClient api = ApiClient.login();
        String accountId = Config.ACCOUNT_ID;

        // 1. List envelopes and pick the first completed one
        JsonElement envelopes = api.get(
                "/api/accounts/" + accountId + "/envelopes?status=completed"
        );

        var items = envelopes.getAsJsonObject().getAsJsonArray("data");
        if (items == null || items.isEmpty()) {
            System.out.println("No completed envelopes found.");
            return;
        }

        String envelopeId = items.get(0).getAsJsonObject()
                .get("id").getAsString();
        System.out.println("Downloading documents for envelope: " + envelopeId);

        // 2. Create downloads directory
        Path downloadDir = Path.of("downloads");
        Files.createDirectories(downloadDir);

        // 3. Download the signed PDF
        byte[] pdf = api.getBytes(
                "/api/accounts/" + accountId + "/envelopes/" + envelopeId + "/documents/combined"
        );
        Path pdfPath = downloadDir.resolve(envelopeId + "-signed.pdf");
        Files.write(pdfPath, pdf);
        System.out.println("Saved signed PDF: " + pdfPath + " (" + pdf.length + " bytes)");

        // 4. Download the signing certificate
        byte[] cert = api.getBytes(
                "/api/accounts/" + accountId + "/envelopes/" + envelopeId + "/documents/certificate"
        );
        Path certPath = downloadDir.resolve(envelopeId + "-certificate.pdf");
        Files.write(certPath, cert);
        System.out.println("Saved certificate: " + certPath + " (" + cert.length + " bytes)");

        System.out.println("\nDone! Files saved to " + downloadDir.toAbsolutePath());
    }
}
