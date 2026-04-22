package com.faizan.taskmanager.Entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
public class VectorEntry {
    private final Long id;
    private final List<Double> vector;
    private final MetaData metadata;

    public VectorEntry(Long id, List<Double> vector, MetaData metadata){
        this.vector= vector;
        this.id= id;
        this.metadata=metadata;
    }

}
