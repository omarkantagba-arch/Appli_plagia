package com.plagicheck.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlagiarismService {

    public double calculateSimilarity(String text1, String text2) {
        String cleanText1 = cleanText(text1);
        String cleanText2 = cleanText(text2);
        
        return cosineSimilarity(cleanText1, cleanText2);
    }

    private String cleanText(String text) {
        return text.toLowerCase()
                .replaceAll("[àáâãäå]", "a")
                .replaceAll("[èéêë]", "e")
                .replaceAll("[ìíîï]", "i")
                .replaceAll("[òóôõö]", "o")
                .replaceAll("[ùúûü]", "u")
                .replaceAll("[^a-z0-9\\s]", "")
                .trim();
    }

    private double cosineSimilarity(String text1, String text2) {
        Map<String, Integer> vector1 = buildTfIdfVector(text1);
        Map<String, Integer> vector2 = buildTfIdfVector(text2);

        Set<String> allWords = new HashSet<>();
        allWords.addAll(vector1.keySet());
        allWords.addAll(vector2.keySet());

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String word : allWords) {
            int val1 = vector1.getOrDefault(word, 0);
            int val2 = vector2.getOrDefault(word, 0);
            
            dotProduct += val1 * val2;
            norm1 += val1 * val1;
            norm2 += val2 * val2;
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return (dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2))) * 100;
    }

    private Map<String, Integer> buildTfIdfVector(String text) {
        String[] words = text.split("\\s+");
        Map<String, Integer> vector = new HashMap<>();
        
        for (String word : words) {
            if (word.length() > 2 && !isStopWord(word)) {
                vector.put(word, vector.getOrDefault(word, 0) + 1);
            }
        }
        
        return vector;
    }

    public double calculateNGramSimilarity(String text1, String text2, int n) {
        Set<String> ngrams1 = generateNGrams(cleanText(text1), n);
        Set<String> ngrams2 = generateNGrams(cleanText(text2), n);

        Set<String> intersection = new HashSet<>(ngrams1);
        intersection.retainAll(ngrams2);

        Set<String> union = new HashSet<>(ngrams1);
        union.addAll(ngrams2);

        if (union.isEmpty()) {
            return 0.0;
        }

        return ((double) intersection.size() / union.size()) * 100;
    }

    private Set<String> generateNGrams(String text, int n) {
        Set<String> ngrams = new HashSet<>();
        String[] words = text.split("\\s+");
        
        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder ngram = new StringBuilder();
            for (int j = 0; j < n; j++) {
                ngram.append(words[i + j]).append(" ");
            }
            ngrams.add(ngram.toString().trim());
        }
        
        return ngrams;
    }

    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of("le", "la", "les", "un", "une", "des", "de", "du", 
                "et", "ou", "mais", "donc", "or", "ni", "car", "ce", "cette", "ces", 
                "mon", "ton", "son", "ma", "ta", "sa", "mes", "tes", "ses");
        return stopWords.contains(word);
    }
}
