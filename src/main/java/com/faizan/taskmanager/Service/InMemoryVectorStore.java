package com.faizan.taskmanager.Service;

import com.faizan.taskmanager.Entity.ScoredEntry;
import com.faizan.taskmanager.Entity.VectorEntry;
import com.faizan.taskmanager.Repository.VectorStore;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryVectorStore implements VectorStore {

    private final List<VectorEntry> store = new ArrayList<>();

    @Override
    public void save(VectorEntry entry) {
        store.add(entry);
    }

    @Override
    public List<ScoredEntry> search(List<Double> queryVector, int topK) {
        return List.of();
    }

    @Override
    public List<VectorEntry> findAll() {
        return new ArrayList<>(store);
    }

    public int size() {
        return store.size();
    }
}
