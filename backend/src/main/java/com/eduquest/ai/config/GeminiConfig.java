package com.eduquest.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
public class GeminiConfig {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.model:gemini-3.8-flash}")
    private String apiModel;

    @Value("${gemini.api.fallback-model:gemini-3.5-flash-lite}")
    private String fallbackModel;

    @Value("${gemini.api.url:}")
    private String apiUrl;

    @Bean
    public RestTemplate geminiRestTemplate(@Value("${gemini.api.connect-timeout-ms:3000}") int connectTimeout,
                                           @Value("${gemini.api.read-timeout-ms:15000}") int readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return new RestTemplate(factory);
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiUrl() {
        return getApiUrlForModel(getApiModel());
    }

    public String getApiUrlForModel(String requestedModel) {
        if (apiUrl != null && !apiUrl.isBlank()) return apiUrl.trim();
        String model = requestedModel == null || requestedModel.isBlank() ? getApiModel() : requestedModel.trim();
        if (!model.matches("[A-Za-z0-9._-]+")) {
            throw new IllegalStateException("Invalid Gemini model name configuration.");
        }
        return "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent";
    }

    public String getApiModel() {
        return apiModel == null || apiModel.isBlank() ? "gemini-3.8-flash" : apiModel.trim();
    }

    public String getFallbackModel() {
        return fallbackModel == null || fallbackModel.isBlank() ? "gemini-3.5-flash-lite" : fallbackModel.trim();
    }

    public boolean hasExplicitApiUrl() {
        return apiUrl != null && !apiUrl.isBlank();
    }
}
