package org.example.vasam.service;

import org.example.vasam.rest.response.ComparisonResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AnalyticsService {
    private final PhraseHandlingService phraseHandlingService;
    private final WordHandlingService wordHandlingService;
    private final UserHandlingService userHandlingService;

    public AnalyticsService(PhraseHandlingService phraseHandlingService, WordHandlingService wordHandlingService, UserHandlingService userHandlingService) {
        this.phraseHandlingService = phraseHandlingService;
        this.wordHandlingService = wordHandlingService;
        this.userHandlingService = userHandlingService;
    }

    public Flux<String> getCommonlyUsedPhraseByUserId(String userId) {
        return phraseHandlingService.getCommonlyUsedPhraseByUserId(userId);
    }

    public Flux<ComparisonResponse> getCommonlyUsedWords(String userId) {
        return wordHandlingService.getCommonlyUsedWordsByUserId(userId)
                .map(word -> ComparisonResponse.builder()
                        .userId(userId)
                        .word(word)
                        .userFrequency(calculateFreq(userId, word))
                        .totalFrequency(getTotalFrequency(word))
                        .averageFrequency(getAverageFrequency(word))
                        .comparisonFrequency(getComparisonFrequency(userId, word) + "%")
                        .build());
    }

    private String getAverageFrequency(String word) {
        int totalFreq = wordHandlingService.getTotalFrequency(word);
        int totalUsers = userHandlingService.getTotalUserCount();
        return String.valueOf(totalFreq / totalUsers);
    }

    private String getComparisonFrequency(String userId, String word) {
        int currentUserFreq = wordHandlingService.getFrequency(userId, word);
        int totalFreq = wordHandlingService.getTotalFrequency(word);
        double percentage = ((double) currentUserFreq / totalFreq) * 100;
        return String.format("%.2f", percentage);
    }

    private String getTotalFrequency(String word) {
        return String.valueOf(wordHandlingService.getTotalFrequency(word));
    }

    private String calculateFreq(String userId, String word) {
        return String.valueOf(wordHandlingService.getFrequency(userId, word));
    }
}
