package com.faizan.taskmanager.Client;

import com.faizan.taskmanager.Dto.QdrantPoint;
import com.faizan.taskmanager.Dto.QdrantSearchResult;

import java.util.List;

public interface QdrantClient {
    void createCollection(String collectionName, int vectorSize);
    void upsertPoint(String collectionName, QdrantPoint point);
    List<QdrantSearchResult> search(
            String collectionName,
            List<Double> queryVector,
            int limit
    );
}
