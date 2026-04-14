package com.faizan.taskmanager.Dto;

import lombok.Data;

import java.util.List;

@Data
public class EmbeddingResponse {
    private List<Double> embedding;
    public List<Double> getEmbedding(){
        return embedding;
    }
    public void setEmbdedding(List<Double> embedding){
        this.embedding= embedding;
    }
}
