package org.example.vasam.rest.api;

import org.example.vasam.rest.response.ComparisonResponse;
import org.example.vasam.rest.response.RecordedResponse;
import org.example.vasam.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @CrossOrigin(origins = "https://regunta.dev")
    @GetMapping(value = "/frequent/phrase")
    public Mono<ResponseEntity<List<RecordedResponse>>> getFrequentPhrases(@RequestParam String userId) {
        return analyticsService.getCommonlyUsedPhraseByUserId(userId)
                .collectList()
                .map(sentences -> ResponseEntity.ok(
                        sentences.stream().map(RecordedResponse::new).toList()
                ));
    }

    @CrossOrigin(origins = "https://regunta.dev")
    @GetMapping(value = "/frequent/words")
    public Mono<ResponseEntity<List<ComparisonResponse>>> getComparisons(@RequestParam String userId) {
        return analyticsService.getCommonlyUsedWords(userId)
                .collectList()
                .map(ResponseEntity::ok);
    }
}
