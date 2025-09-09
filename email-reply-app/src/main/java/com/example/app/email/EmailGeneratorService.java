package com.example.app.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class EmailGeneratorService {
	private final WebClient webClient;
	private final String apiKey;
	private final String model;

	public EmailGeneratorService(@Value("${app.gemini.apiKey:}") String apiKey,
								 @Value("${app.gemini.model:gemini-1.5-flash}") String model) {
		this.apiKey = apiKey;
		this.model = model;
		this.webClient = WebClient.builder()
				.baseUrl("https://generativelanguage.googleapis.com")
				.build();
	}

	public String generateReply(String incomingEmail) {
		if (apiKey == null || apiKey.isBlank()) {
			return "[Gemini API key not configured. Set GEMINI_API_KEY env var.]";
		}
		String systemPrompt = "You are an assistant that writes polite, concise, and professional email replies. Respond directly as an email body without extra commentary.";

		String json = "{\n" +
				"  \"contents\": [\n" +
				"    {\n" +
				"      \"role\": \"user\",\n" +
				"      \"parts\": [ { \"text\": " + quote(systemPrompt + "\n\nIncoming email:\n\n" + incomingEmail) + " } ]\n" +
				"    }\n" +
				"  ]\n" +
				"}";

		String path = "/v1beta/models/" + model + ":generateContent?key=" + apiKey;

		return webClient.post()
				.uri(path)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(json))
				.retrieve()
				.bodyToMono(String.class)
				.map(EmailGeneratorService::extractText)
				.onErrorResume(ex -> Mono.just("[Error from Gemini: " + ex.getMessage() + "]"))
				.blockOptional()
				.orElse("[No response]");
	}

	private static String quote(String text) {
		String escaped = text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
		return "\"" + escaped + "\"";
	}

	private static String extractText(String raw) {
		// naive extract to keep dependencies light
		// looks for "text":"..." and returns first occurrence
		int idx = raw.indexOf("\"text\":");
		if (idx < 0) return raw;
		int start = raw.indexOf('"', idx + 7);
		if (start < 0) return raw;
		start++;
		int end = start;
		boolean escape = false;
		while (end < raw.length()) {
			char c = raw.charAt(end);
			if (escape) {
				escape = false;
				end++;
				continue;
			}
			if (c == '\\') {
				escape = true;
				end++;
				continue;
			}
			if (c == '"') break;
			end++;
		}
		String text = raw.substring(start, end)
				.replace("\\n", "\n")
				.replace("\\\"", "\"")
				.replace("\\\\", "\\");
		return text;
	}
}