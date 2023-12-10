package org.example.vasam.service;

import org.example.vasam.model.WordFrequency;
import org.example.vasam.repository.WordFrequencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
public class WordHandlingService {
    private final WordFrequencyRepository wordFrequencyRepository;

    public WordHandlingService(WordFrequencyRepository wordFrequencyRepository) {
        this.wordFrequencyRepository = wordFrequencyRepository;
    }

    public Mono<String> recordWordForUser(String userId, String word) {
        return Mono.defer(() -> {
            try {
                WordFrequency optionalPhraseFrequency = wordFrequencyRepository.getWordFrequencyByUserIdAndWord(userId, word);
                if (!ObjectUtils.isEmpty(optionalPhraseFrequency)) {
                    wordFrequencyRepository.incrementFrequencyCounterByUserIdAndPhrase(userId, word);
                } else {
                    wordFrequencyRepository.save(WordFrequency.builder().userId(userId).word(word).frequency(1).build());
                }
                return Mono.just("Success");
            } catch (Exception e) {
                return Mono.error(e);
            }
        });
    }

    public Flux<String> getCommonlyUsedWordsByUserId(String userId) {
        return Mono.just(wordFrequencyRepository.getMostFrequentWordsByUserId(userId))
                .flatMapMany(wordFrequencyList -> Flux.fromIterable(wordFrequencyList)
                        .map(WordFrequency::getWord));
    }

    public int getTotalFrequency(String word) {
        List<WordFrequency> wordFrequencyList = wordFrequencyRepository.getWordFrequenciesByWord(word);
        if (ObjectUtils.isEmpty(wordFrequencyList)) return 0;
        return wordFrequencyList.stream()
                .mapToInt(WordFrequency::getFrequency)
                .sum();
    }


    public int getFrequency(String userId, String word) {
        WordFrequency optionalPhraseFrequency = wordFrequencyRepository.getWordFrequencyByUserIdAndWord(userId, word);
        return (ObjectUtils.isEmpty(optionalPhraseFrequency)) ? 0 : optionalPhraseFrequency.getFrequency();
    }

}
