# Vanilla API — Java Samples

Runnable Java examples for the Vanilla e-signature API.

## Prerequisites

- Java 17+
- Gradle (wrapper included)

## Feature Status

Most samples work with the current Vanilla Esign API. The following samples demonstrate **planned features** that are not yet implemented:

- `samples/UseTemplates.java` — Templates (planned)
- `samples/BulkSend.java` — Bulk Send (planned)

## Setup

```bash
git clone <repo-url> && cd vanilla-samples-java
```

Set the required environment variables:

```bash
export VANILLA_API_URL="https://demo.ravenbill.com"  # or https://www.ravenbill.com for production
export VANILLA_EMAIL="you@example.com"
export VANILLA_PASSWORD="your-password"
export VANILLA_ACCOUNT_ID="your-account-id"
```

## Run a sample

```bash
./gradlew run -PmainClass=samples.CreateAndSendEnvelope
```

Replace `samples.CreateAndSendEnvelope` with any sample class:

| Class | Description |
|---|---|
| `samples.CreateAndSendEnvelope` | Create a draft envelope and send it |
| `samples.AddRecipients` | Add recipients and signature tabs |
| `samples.UseTemplates` | List and use templates |
| `samples.CheckStatus` | Query envelope status and poll |
| `samples.DownloadDocuments` | Download signed PDF and certificate |
| `samples.WebhookHandler` | Local HTTP server for webhook events |
| `samples.BulkSend` | Read CSV and send envelopes in bulk |
| `samples.GraphqlQueries` | GraphQL queries and mutations |

## License

MIT — Copyright 2026 Ravenbill
