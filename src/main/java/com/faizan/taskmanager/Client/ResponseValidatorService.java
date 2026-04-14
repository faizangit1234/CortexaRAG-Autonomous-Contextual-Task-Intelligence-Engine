package com.faizan.taskmanager.Client;

import com.faizan.taskmanager.Entity.ScoredEntry;

import java.util.List;

public interface ResponseValidatorService {
    public String validate(String llmOutput, List<ScoredEntry> retrievedEntries);
}
