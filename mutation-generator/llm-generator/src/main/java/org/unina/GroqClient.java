package org.unina;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * Minimal Groq (OpenAI-compatible) chat client.
 * Honours the 429 protocol of the experiment: on per-minute rate-limit / transient errors it
 * pauses (Retry-After when present, otherwise exponential backoff) and retries.
 * Supports a pool of API keys: when a key is dead (401/403 expired) or its DAILY quota is
 * exhausted (429 with per-day limit), it rotates to the next key. The keys are read from the
 * environment, never hardcoded.
 */
public class GroqClient {
    private static final String ENDPOINT = "https://api.groq.com/openai/v1/chat/completions";

    private static final int MIN_MAX_TOKENS = 2000;

    private final List<String> apiKeys;
    private int keyIndex = 0;
    private final String model;
    private final double temperature;
    private int maxTokens; // adaptively shrunk if the request exceeds the per-minute token cap (413)
    private final int maxRetries;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    public GroqClient(List<String> apiKeys, String model, double temperature, int maxTokens, int maxRetries) {
        if (apiKeys == null || apiKeys.isEmpty()) {
            throw new IllegalArgumentException("At least one API key is required");
        }
        this.apiKeys = apiKeys;
        this.model = model;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
        this.maxRetries = maxRetries;
    }

    private String currentKey() {
        return apiKeys.get(keyIndex);
    }

    /** Switches to the next key. Returns false if there is no key left. */
    private boolean rotateKey(String why) {
        if (keyIndex + 1 >= apiKeys.size()) {
            System.err.printf("  [Groq] %s and no more keys left (used %d/%d).%n", why, keyIndex + 1, apiKeys.size());
            return false;
        }
        keyIndex++;
        System.out.printf("  [Groq] %s -> rotating to key %d/%d.%n", why, keyIndex + 1, apiKeys.size());
        return true;
    }

    /** Returns the assistant message content, or null if every attempt failed. */
    public String chat(String systemPrompt, String userPrompt) {
        long backoffSeconds = 20;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Authorization", "Bearer " + currentKey())
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofMinutes(3))
                    .POST(HttpRequest.BodyPublishers.ofString(buildRequestBody(systemPrompt, userPrompt)))
                    .build();
            try {
                HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
                int status = response.statusCode();
                String respBody = response.body();

                if (status == 200) {
                    return extractContent(respBody);
                }

                // 413: the single request is larger than the per-minute token cap -> shrink and retry.
                if (status == 413 && maxTokens > MIN_MAX_TOKENS) {
                    int reduced = Math.max(MIN_MAX_TOKENS, maxTokens / 2);
                    System.out.printf("  [Groq] HTTP 413 (request too large). Reducing max_tokens %d -> %d and retrying.%n",
                            maxTokens, reduced);
                    maxTokens = reduced;
                    continue;
                }

                // Expired / invalid key -> rotate (no point waiting).
                if (status == 401 || status == 403) {
                    if (rotateKey("HTTP " + status + " (key invalid/expired)")) continue;
                    return null;
                }

                if (status == 429) {
                    if (isDailyQuota(respBody)) {
                        // This key is out for the day: rotate instead of waiting.
                        if (rotateKey("HTTP 429 (daily quota exhausted)")) continue;
                        return null;
                    }
                    long wait = retryAfterSeconds(response).orElse(backoffSeconds);
                    System.out.printf("  [Groq] HTTP 429 per-minute (attempt %d/%d). Pausing %ds before retry...%n",
                            attempt, maxRetries, wait);
                    sleep(wait);
                    backoffSeconds = Math.min(backoffSeconds * 2, 120);
                    continue;
                }

                if (status >= 500) {
                    System.out.printf("  [Groq] HTTP %d (attempt %d/%d). Pausing %ds before retry...%n",
                            status, attempt, maxRetries, backoffSeconds);
                    sleep(backoffSeconds);
                    backoffSeconds = Math.min(backoffSeconds * 2, 120);
                    continue;
                }

                // Non-retryable (e.g. 400, or 413 already at the floor): surface and stop.
                System.err.printf("  [Groq] Non-retryable HTTP %d: %s%n", status, truncate(respBody));
                return null;
            } catch (Exception e) {
                System.err.printf("  [Groq] Request failed (attempt %d/%d): %s%n", attempt, maxRetries, e.getMessage());
                sleep(backoffSeconds);
                backoffSeconds = Math.min(backoffSeconds * 2, 120);
            }
        }
        System.err.println("  [Groq] Giving up after " + maxRetries + " attempts.");
        return null;
    }

    /** True when a 429 body indicates a per-DAY limit (TPD/RPD) rather than per-minute (TPM/RPM). */
    private boolean isDailyQuota(String body) {
        if (body == null) return false;
        String b = body.toLowerCase();
        return b.contains("per day") || b.contains("(tpd)") || b.contains("(rpd)")
                || b.contains("tokens per day") || b.contains("requests per day");
    }

    private String buildRequestBody(String systemPrompt, String userPrompt) {
        ObjectNode root = mapper.createObjectNode();
        root.put("model", model);
        root.put("temperature", temperature);
        root.put("max_tokens", maxTokens);
        ArrayNode messages = root.putArray("messages");
        ObjectNode sys = messages.addObject();
        sys.put("role", "system");
        sys.put("content", systemPrompt);
        ObjectNode usr = messages.addObject();
        usr.put("role", "user");
        usr.put("content", userPrompt);
        try {
            return mapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException("Cannot serialize Groq request", e);
        }
    }

    private String extractContent(String responseBody) {
        try {
            JsonNode root = mapper.readTree(responseBody);
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            return content.isMissingNode() ? null : content.asText();
        } catch (Exception e) {
            System.err.println("  [Groq] Cannot parse response: " + e.getMessage());
            return null;
        }
    }

    private java.util.OptionalLong retryAfterSeconds(HttpResponse<String> response) {
        return response.headers().firstValue("Retry-After")
                .map(String::trim)
                .filter(s -> s.matches("\\d+"))
                .map(Long::parseLong)
                .map(java.util.OptionalLong::of)
                .orElse(java.util.OptionalLong.empty());
    }

    private void sleep(long seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String truncate(String s) {
        if (s == null) return "";
        return s.length() > 500 ? s.substring(0, 500) + "..." : s;
    }
}
