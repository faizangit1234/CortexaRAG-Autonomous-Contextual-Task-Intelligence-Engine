package com.faizan.taskmanager.Dto;

import lombok.Data;

@Data
public class EmbeddingRequest {
    private String model;
    private String prompt;


    public EmbeddingRequest( String prompt){
        this.prompt= prompt;
    }
    public EmbeddingRequest(String model, String prompt){
        this.model= model;
        this.prompt= prompt;
    }
}
