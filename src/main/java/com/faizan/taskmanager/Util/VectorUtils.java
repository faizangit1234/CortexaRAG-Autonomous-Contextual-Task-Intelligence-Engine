package com.faizan.taskmanager.Util;

import java.util.List;

public class VectorUtils {

    public static double cosineSimilarity(List<Double> A , List<Double> B){

        // safety check
        if(A == null || B == null){
            throw new IllegalArgumentException("Vectors must not be null");
        }

        // safety check 2
        if(A.size() != B.size()){
            throw new IllegalArgumentException("Vectors must be of same size");
        }

        //compute dot product
        double dotProduct = 0.0;

        //compute magnitudes
        double normA = 0.0;
        double normB = 0.0;

        for(int i = 0 ; i < A.size() ; i++){
            double a = A.get(i);
            double b = B.get(i);

            dotProduct += a * b;
            normA += a * a;
            normB += b * b;

            // prevent division by zero
            if(normA == 0 || normB == 0){
                return 0.0;
            }
        }
            // final cosine similarity
            return dotProduct / ( Math.sqrt(normA) * Math.sqrt(normB));
    }
}
