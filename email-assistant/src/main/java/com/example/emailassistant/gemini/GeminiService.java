package com.example.emailassistant.gemini;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    @Value("${gemini.base-url}")
    private String baseUrl;

    public String generateResponseEmail(String incomingEmailText) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY is not configured");
        }

        String url = baseUrl + "/models/" + model + ":generateContent";

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", "Write a polite, concise email reply to the following:\n\n" + incomingEmailText);
        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-goog-api-key", apiKey);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            Map response = restTemplate.postForObject(url, entity, Map.class);
            if (response == null) {
                return "";
            }
            // Navigate: candidates[0].content.parts[0].text
            List candidates = (List) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) return "";
            Map first = (Map) candidates.get(0);
            Map rContent = (Map) first.get("content");
            if (rContent == null) return "";
            List parts = (List) rContent.get("parts");
            if (parts == null || parts.isEmpty()) return "";
            Map firstPart = (Map) parts.get(0);
            Object text = firstPart.get("text");
            return text != null ? text.toString() : "";
        } catch (RestClientException e) {
            throw new RuntimeException("Gemini API error: " + e.getMessage(), e);
        }
    }
}

