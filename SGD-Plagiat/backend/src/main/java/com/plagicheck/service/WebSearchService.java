package com.plagicheck.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSearchService {

    private final PlagiarismService plagiarismService;

    @Value("${serpapi.key}")
    private String serpApiKey;

    @Value("${serpapi.url}")
    private String serpApiUrl;

    public double searchWebForPlagiarism(String text) {
        try {
            log.info("Starting web search for plagiarism detection");
            
            // Extraire des snippets significatifs du texte
            List<String> snippets = extractSignificantSnippets(text, 5);
            log.info("Extracted {} significant snippets for web search", snippets.size());
            
            double maxSimilarity = 0.0;
            
            for (int i = 0; i < snippets.size(); i++) {
                String snippet = snippets.get(i);
                log.debug("Searching snippet {}/{}: {}", i + 1, snippets.size(), 
                         snippet.substring(0, Math.min(50, snippet.length())) + "...");
                
                double similarity = simulateWebSearch(snippet);
                maxSimilarity = Math.max(maxSimilarity, similarity);
            }
            
            log.info("Web search completed. Max similarity found: {}%", maxSimilarity);
            return maxSimilarity;
        } catch (Exception e) {
            log.error("Error during web search: {}", e.getMessage());
            // En cas d'erreur, retourner 0 pour ne pas bloquer le processus
            return 0.0;
        }
    }

    private List<String> extractSignificantSnippets(String text, int count) {
        List<String> snippets = new ArrayList<>();
        
        // Nettoyer le texte
        String cleanText = text.replaceAll("\\s+", " ").trim();
        
        // Diviser en phrases
        String[] sentences = cleanText.split("[.!?]+");
        
        // Prendre les phrases les plus longues (plus susceptibles d'être significatives)
        List<String> sortedSentences = new ArrayList<>();
        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (trimmed.length() > 50) { // Ignorer les phrases trop courtes
                sortedSentences.add(trimmed);
            }
        }
        
        // Trier par longueur décroissante
        sortedSentences.sort((a, b) -> Integer.compare(b.length(), a.length()));
        
        // Prendre les N premières
        for (int i = 0; i < Math.min(count, sortedSentences.size()); i++) {
            snippets.add(sortedSentences.get(i));
        }
        
        return snippets;
    }

    private double simulateWebSearch(String snippet) {
        try {
            // Encoder la requête pour l'URL
            String encodedQuery = URLEncoder.encode(snippet, StandardCharsets.UTF_8);
            
            // Construire l'URL de l'API SerpApi
            String apiUrl = String.format(
                "%s?engine=google&q=%s&api_key=%s&num=5",
                serpApiUrl, encodedQuery, serpApiKey
            );
            
            log.debug("Calling SerpApi for snippet: {}...", snippet.substring(0, Math.min(50, snippet.length())));
            
            // Appeler l'API
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(apiUrl, String.class);
            
            if (response == null || response.isEmpty()) {
                log.warn("Empty response from SerpApi");
                return 0.0;
            }
            
            // Parser la réponse JSON
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            
            // Vérifier s'il y a des résultats organiques
            if (!jsonResponse.has("organic_results")) {
                log.debug("No organic results found");
                return 0.0;
            }
            
            JsonArray organicResults = jsonResponse.getAsJsonArray("organic_results");
            double maxSimilarity = 0.0;
            
            // Comparer le snippet avec chaque résultat
            for (JsonElement resultElement : organicResults) {
                JsonObject result = resultElement.getAsJsonObject();
                
                // Extraire le snippet du résultat
                String resultSnippet = "";
                if (result.has("snippet")) {
                    resultSnippet = result.get("snippet").getAsString();
                }
                
                // Ajouter le titre si disponible
                if (result.has("title")) {
                    resultSnippet = result.get("title").getAsString() + " " + resultSnippet;
                }
                
                if (!resultSnippet.isEmpty()) {
                    // Calculer la similarité entre le snippet original et le résultat
                    double similarity = plagiarismService.calculateSimilarity(snippet, resultSnippet);
                    maxSimilarity = Math.max(maxSimilarity, similarity);
                    
                    if (similarity > 50) {
                        log.info("High similarity found: {}% with source: {}", 
                                similarity, result.has("link") ? result.get("link").getAsString() : "unknown");
                    }
                }
            }
            
            return maxSimilarity;
            
        } catch (Exception e) {
            log.error("Error during SerpApi search: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    public boolean shouldPerformWebSearch(double localScore) {
        // Méthode conservée pour compatibilité
        // La recherche web est maintenant toujours effectuée
        return true;
    }

    public List<String> getWebSources(String text) {
        // Méthode pour récupérer les sources web détectées
        List<String> sources = new ArrayList<>();
        
        try {
            List<String> snippets = extractSignificantSnippets(text, 3);
            
            for (String snippet : snippets) {
                String encodedQuery = URLEncoder.encode(snippet, StandardCharsets.UTF_8);
                String apiUrl = String.format(
                    "%s?engine=google&q=%s&api_key=%s&num=3",
                    serpApiUrl, encodedQuery, serpApiKey
                );
                
                RestTemplate restTemplate = new RestTemplate();
                String response = restTemplate.getForObject(apiUrl, String.class);
                
                if (response != null) {
                    JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
                    
                    if (jsonResponse.has("organic_results")) {
                        JsonArray organicResults = jsonResponse.getAsJsonArray("organic_results");
                        
                        for (JsonElement resultElement : organicResults) {
                            JsonObject result = resultElement.getAsJsonObject();
                            if (result.has("link")) {
                                sources.add(result.get("link").getAsString());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting web sources: {}", e.getMessage());
        }
        
        return sources;
    }
}
