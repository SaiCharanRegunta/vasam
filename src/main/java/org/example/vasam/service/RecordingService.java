package org.example.vasam.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class RecordingService {
    private final PhraseHandlingService phraseHandlingService;
    private final WordHandlingService wordHandlingService;
    private final UserHandlingService userHandlingService;

    public RecordingService(PhraseHandlingService phraseHandlingService, WordHandlingService wordHandlingService, UserHandlingService userHandlingService) {
        this.phraseHandlingService = phraseHandlingService;
        this.wordHandlingService = wordHandlingService;
        this.userHandlingService = userHandlingService;
    }

    public Mono<String> recordSentenceForUser(String userId, String sentence) {
        if (!userHandlingService.doesUserExists(userId)) userHandlingService.register(userId);
        Flux<String> wordsFlux = Flux.fromIterable(splitSentenceToWords(sentence));
        Flux<String> phrasesFlux = Flux.fromIterable(splitSentenceToPhrases(sentence));

        return Mono.zip(
                        wordsFlux.flatMap(word -> wordHandlingService.recordWordForUser(userId, word)).then(),
                        phrasesFlux.flatMap(phrase -> phraseHandlingService.recordPhraseForUser(userId, phrase)).then()
                )
                .thenReturn("Success")
                .onErrorResume(e -> Mono.error(new RuntimeException("Error recording sentence", e)));
    }

    private List<String> splitSentenceToWords(String sentence) {
        StringTokenizer tokenizer = new StringTokenizer(sentence);
        List<String> words = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            words.add(tokenizer.nextToken());
        }
        return words;
    }

    private List<String> splitSentenceToPhrases(String sentence) {
        StringTokenizer tokenizer = new StringTokenizer(sentence, ".!?");
        List<String> phrases = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            phrases.add(tokenizer.nextToken());
        }
        return phrases;
    }
}
