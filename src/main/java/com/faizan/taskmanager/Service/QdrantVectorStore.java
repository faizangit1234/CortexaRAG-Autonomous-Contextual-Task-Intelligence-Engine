package com.faizan.taskmanager.Service;

import com.faizan.taskmanager.Client.QdrantClient;
import com.faizan.taskmanager.Dto.QdrantPoint;
import com.faizan.taskmanager.Dto.QdrantSearchResult;
import com.faizan.taskmanager.Entity.MetaData;
import com.faizan.taskmanager.Entity.ScoredEntry;
import com.faizan.taskmanager.Entity.VectorEntry;
import com.faizan.taskmanager.Repository.VectorStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Primary
@RequiredArgsConstructor
public class QdrantVectorStore implements VectorStore {
    private final QdrantClient qdrantClient;
    private final String COLLECTION="tasks";

    @Override
    public void save(VectorEntry entry) {

        Map<String , Object> payload = Map.of(
                "title",entry.getMetadata().getTitle(),
                "description", entry.getMetadata().getDescription()
        );

        QdrantPoint point = new QdrantPoint();
        point.setId(entry.getId());
        point.setVector(entry.getVector());
        point.setPayload(payload);

        qdrantClient.upsertPoint(COLLECTION,point);
    }

    @Override
    public List<ScoredEntry> search(List<Double> queryVector, int topK) {

        List<QdrantSearchResult> results = qdrantClient.search(COLLECTION,queryVector,topK);

        return results.stream().map(r->{

            if(r.getPayload() == null){
                throw new RuntimeException("Payload missing from qdrant");
            }

            MetaData metaData= new MetaData(
                    (String) r.getPayload().get("title"),
                    (String) r.getPayload().get("description")
            );

            VectorEntry entry = new VectorEntry(
                    r.getId(),
                    null,
                    metaData
            );
        return new ScoredEntry(entry,r.getScore());

        }).toList();
    }

    @Override
    public List<VectorEntry> findAll() {
        return List.of();
    }

    @Override
    public int size() {
        return 0;
    }
}
