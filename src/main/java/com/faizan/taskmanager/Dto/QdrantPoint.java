package com.faizan.taskmanager.Dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QdrantPoint {
    private Long id;
    private List<Double> vector;
    private Map<String,Object> payload;
}
