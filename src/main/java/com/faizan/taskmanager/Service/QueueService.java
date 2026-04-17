package com.faizan.taskmanager.Service;

import com.faizan.taskmanager.Entity.ScoredEntry;

import java.util.List;

public interface QueueService {
    void enqueue(String query, List<ScoredEntry> results);
}
