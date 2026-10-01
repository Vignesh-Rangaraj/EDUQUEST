package com.eduquest.ai.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LanguageService {
    private static final Map<String, String> LANGUAGES = Map.of(
            "en", "English", "ta", "Tamil", "hi", "Hindi",
            "ml", "Malayalam", "te", "Telugu", "kn", "Kannada");

    public String normalize(String value) {
        if (value == null || value.isBlank()) return "en";
        String candidate = value.trim().toLowerCase();
        for (Map.Entry<String, String> language : LANGUAGES.entrySet()) {
            if (language.getKey().equals(candidate) || language.getValue().equalsIgnoreCase(candidate)) return language.getKey();
        }
        throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Unsupported language");
    }

    public String displayName(String code) {
        return LANGUAGES.getOrDefault(normalize(code), "English");
    }

    public Map<String, String> supportedLanguages() { return LANGUAGES; }
}
