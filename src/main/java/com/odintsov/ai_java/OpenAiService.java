package com.odintsov.ai_java;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
class OpenAiService {
    // ключ взято на https://openrouter.ai/settings/keys
    // якщо виникає помилка AI: 401 Unauthorized on POST request for, перегенерувати ключ
    @Value("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")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // https://x.ai/api
    // https://platform.openai.com/api-keys
    // https://developer.puter.com/tutorials/free-unlimited-openai-api/ !!! FREE

    // private static final String API_URL = "https://api.x.ai/v1/chat/completions"; // Grok API
    // private static final String API_URL = "https://api.openai.com/v1/chat/completions"; // ChatGPT
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    public String getJoke(String topic) {
        try {
            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            headers.set("HTTP-Referer", "http://localhost:8080");
            headers.set("X-Title", "Генератор жартів");

            Map<String, Object> message = Map.of(
                    "role", "user",
                    "content", "Згенеруй казку на цю тему: " + topic
            );

            Map<String, Object> requestBody = Map.of( // ліміт - 50 запитів на день, 20 на хвилину
                    "model", "openai/gpt-4o-mini",
                    // "model", "openai/gpt-4o-mini",
                    // "model", "mistralai/mistral-7b-instruct:free",
                    "messages", List.of(message),
                    "temperature", 0.9, // чим вище значення, тим більше маячні
                    "max_tokens", 300 // (токен ≈ 3-4 символи або 3/4 слова)
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(API_URL, request, Map.class);

            if (response != null && response.containsKey("choices")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> msg = (Map<String, Object>) choices.get(0).get("message");
                    return (String) msg.get("content");
                }
            }
            return "Не вдалося отримати жарт";
        } catch (Exception e) {
            return "Помилка AI: " + e.getMessage();
        }
    }
}
