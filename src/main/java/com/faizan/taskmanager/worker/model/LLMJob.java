package com.faizan.taskmanager.worker.model;

import com.faizan.taskmanager.Entity.ScoredEntry;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class LLMJob {
    private String query;
    private List<ScoredEntry> results;
}
