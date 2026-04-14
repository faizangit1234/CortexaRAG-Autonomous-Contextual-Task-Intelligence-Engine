package com.faizan.taskmanager.Dto;

import lombok.Data;

import java.util.Map;

@Data
public class QdrantSearchResult {

    private Long id;
    private double score;
    private Map<String,Object> payload;
}
