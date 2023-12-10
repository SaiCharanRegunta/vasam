package org.example.vasam.service;

import org.example.vasam.model.PhraseFrequency;
import org.example.vasam.repository.PhraseFrequencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class PhraseHandlingService {
    private final PhraseFrequencyRepository phraseFrequencyRepository;

    public PhraseHandlingService(PhraseFrequencyRepository phraseFrequencyRepository) {
        this.phraseFrequencyRepository = phraseFrequencyRepository;
    }

    public Mono<String> recordPhraseForUser(String userId, String phrase) {
        return Mono.defer(() -> {
            try {
                PhraseFrequency optionalPhraseFrequency = phraseFrequencyRepository.getPhraseFrequencyByUserIdAndPhrase(userId, phrase);
                if (!ObjectUtils.isEmpty(optionalPhraseFrequency)) {
                    phraseFrequencyRepository.incrementFrequencyCounterByUserIdAndPhrase(userId, phrase);
                } else {
                    phraseFrequencyRepository.save(PhraseFrequency.builder().userId(userId).phrase(phrase).frequency(1).build());
                }
                return Mono.just("Success");
            } catch (Exception e) {
                return Mono.error(e);
            }
        });
    }

    public Flux<String> getCommonlyUsedPhraseByUserId(String userId) {
        return Flux.fromIterable(phraseFrequencyRepository.getMostFrequentPhraseByUserId(userId))
                .map(PhraseFrequency::getPhrase);
    }
}
