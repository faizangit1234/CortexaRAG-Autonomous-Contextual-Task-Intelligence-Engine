package com.faizan.taskmanager.Service;

import com.faizan.taskmanager.Dto.EmbeddingRequest;
import com.faizan.taskmanager.Entity.ScoredEntry;
import com.faizan.taskmanager.Entity.VectorEntry;
import com.faizan.taskmanager.Repository.VectorStore;
import com.faizan.taskmanager.Util.VectorUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RetrieverService {

    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;

    public RetrieverService(EmbeddingService embeddingService , VectorStore vectorStore){
        this.embeddingService= embeddingService;
        this.vectorStore= vectorStore;
    }

    public List<VectorEntry> search(String query, int topK){
        //convert query to vector
        List<Double> queryVector= embeddingService.embedText(new EmbeddingRequest(query));

        //get all stored vectors
        List<VectorEntry> allEntries = vectorStore.findAll();

        //store each entry
        List<ScoredEntry> scored = new ArrayList<>();

        for(VectorEntry entry : allEntries){
            double score = VectorUtils.cosineSimilarity(
                    queryVector,
                    entry.getVector()
            );

            // 🔥 Add threshold filter
            if (score > 0.45) {
                scored.add(new ScoredEntry(entry, score));
            }


            System.out.println("------------");
            System.out.println("QUERY: " + query);
            System.out.println("TEXT: " + entry.getMetadata());
            System.out.println("SCORE: " + score);


        }

        // sort descending
        scored.sort((a,b)-> Double.compare(b.getScore(),a.getScore()));
        return scored.stream()
                .limit(topK)
                .map(ScoredEntry::getEntry)
                .toList();
    }

    public List<ScoredEntry> retrieve (
            List<Double> queryVector ,
            int topK,
            double threshold
    ){
        List<ScoredEntry> results =
                vectorStore.search(queryVector,topK);

        List<ScoredEntry> filtered = results.stream()
                .filter(r-> r.getScore() >= threshold)
                .toList();

        System.out.println("RETRIEVAL RESULTS: ");
        for (ScoredEntry r : filtered){
            System.out.println(
                    r.getEntry().getMetadata()+
                            " | score="+ r.getScore()
            );
        }
        return filtered;
    }

    /* old retrieve method used when vectors were stored in memory and we manually calculated
     SIMILARITY BY COSINE AND SORTED MANUALLY AND RETURNED TOP FEW RESULTS BUT NOW QDRANT DID IT AUTOMATICALLY


    public List<ScoredEntry> retrieve(
            List<Double> queryVector,
            int topK,
            double threshold
    ) {

        List<VectorEntry> allEntries = vectorStore.findAll();

        List<ScoredEntry> scored = new ArrayList<>();

        for (VectorEntry entry : allEntries) {

            double score = VectorUtils.cosineSimilarity(
                    queryVector,
                    entry.getVector()
            );

            if (score > threshold) {
                scored.add(new ScoredEntry(entry, score));
            }

            System.out.println("------------");
            System.out.println("TEXT: " + entry.getMetadata());
            System.out.println("SCORE: " + score);
        }

        // sort descending
        scored.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        return scored.stream()
                .limit(topK)
                .toList();
    }

    */
}
