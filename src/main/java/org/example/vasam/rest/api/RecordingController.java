package org.example.vasam.rest.api;

import org.example.vasam.rest.request.RecordRequest;
import org.example.vasam.rest.response.RecordedResponse;
import org.example.vasam.service.RecordingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class RecordingController {
    private final RecordingService recordingService;

    public RecordingController(RecordingService recordingService) {
        this.recordingService = recordingService;
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @PostMapping(value = "/record")
    public Mono<ResponseEntity<RecordedResponse>> recordSentences(@RequestBody RecordRequest recordRequest) {
        return recordingService.recordSentenceForUser(recordRequest.getUserId(), recordRequest.getTranscript())
                .map(sentence -> ResponseEntity.ok(new RecordedResponse(sentence)));
    }
}
