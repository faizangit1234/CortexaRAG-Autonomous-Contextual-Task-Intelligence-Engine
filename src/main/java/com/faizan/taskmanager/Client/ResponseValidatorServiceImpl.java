package com.faizan.taskmanager.Client;

import com.faizan.taskmanager.Entity.ScoredEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ResponseValidatorServiceImpl implements ResponseValidatorService {

    private static final double TOKEN_THRESHOLD = 0.4;

    @Override
    public String validate(String llmOutput, List<ScoredEntry> retrievedEntries) {

        if (retrievedEntries == null || retrievedEntries.isEmpty()) {
            return "INSUFFICIENT_CONTEXT";
        }

        List<String> candidates = extractDescriptions(retrievedEntries);

        String cleaned = normalize(llmOutput);

        // ✅ 1. Exact match (fastest)
        for (String candidate : candidates) {
            if (candidate.equalsIgnoreCase(llmOutput.trim())) {
                //log prompt
                log.info("EXACT MATCH");
                return candidate;
            }
        }

        // ✅ 2. Normalized match
        for (String candidate : candidates) {
            if (normalize(candidate).equals(cleaned)) {
                log.info("NORMALIZED MATCH");
                return candidate;
            }
        }

        // ✅ 3. Token overlap (lightweight fuzzy)
        String tokenMatch = tokenMatch(cleaned, candidates);
        if (tokenMatch != null) {
            log.info("TOKEN MATCH");
            return tokenMatch;
        }

        // ✅ 4. Fallback → trust Qdrant ranking
        log.info("NO MATCH.FIRST ENTRY");
        return candidates.getFirst();
    }

    // ------------------------

    private List<String> extractDescriptions(List<ScoredEntry> entries) {
        return entries.stream()
                .map(e -> (String) e.getEntry().getMetadata().getDescription())
                .toList();
    }

    private String normalize(String input) {
        if (input == null) return "";

        return input.toLowerCase()
                .replaceAll("[^a-z0-9 ]", "")
                .trim();
    }

    private String tokenMatch(String output, List<String> candidates) {

        Set<String> outputTokens = new HashSet<>(List.of(output.split("\\s+")));

        double bestScore = 0;
        String bestMatch = null;

        for (String candidate : candidates) {

            Set<String> candidateTokens = new HashSet<>(
                    List.of(normalize(candidate).split("\\s+"))
            );

            Set<String> intersection = new HashSet<>(outputTokens);
            intersection.retainAll(candidateTokens);

            double score = (double) intersection.size() / candidateTokens.size();

            if (score > bestScore) {
                bestScore = score;
                bestMatch = candidate;
            }
        }

        return bestScore >= TOKEN_THRESHOLD ? bestMatch : null;
    }
}