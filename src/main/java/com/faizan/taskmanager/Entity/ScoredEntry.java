package com.faizan.taskmanager.Entity;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
public class ScoredEntry {


    public VectorEntry getEntry() {
        return entry;
    }

    public double getScore() {
        return score;
    }

    private final VectorEntry entry;
    private final double score;

    public ScoredEntry(VectorEntry entry, double score) {
        this.entry = entry;
        this.score = score;
    }

}