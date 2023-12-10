package org.example.vasam.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonResponse {
    String userId;
    String word;
    String userFrequency;
    String comparisonFrequency;
    String averageFrequency;
    String totalFrequency;
}
