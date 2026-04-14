package com.faizan.taskmanager.Repository;

import com.faizan.taskmanager.Entity.ScoredEntry;
import com.faizan.taskmanager.Entity.VectorEntry;

import java.util.List;

public interface VectorStore {
    void save(VectorEntry entry);
    List<ScoredEntry> search(List<Double> queryVector, int topK);
    List<VectorEntry> findAll();
    public int size();
}
