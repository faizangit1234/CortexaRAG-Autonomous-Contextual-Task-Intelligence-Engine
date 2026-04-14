package com.faizan.taskmanager.Client;

import com.faizan.taskmanager.Dto.QdrantPoint;
import com.faizan.taskmanager.Dto.QdrantSearchResult;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class QdrantClientImpl implements QdrantClient{

    private final RestTemplate restTemplate;
    private final String baseUrl= "http://localhost:6333";

    public QdrantClientImpl(RestTemplate restTemplate){
        this.restTemplate= restTemplate;
    }


    @Override
    public void createCollection(String collectionName, int vectorSize) {
        String url = baseUrl+"/collections/"+collectionName;

        Map<String,Object> body = Map.of(
                "vectors",Map.of(
                        "size", vectorSize,
                        "distance","Cosine"
                )
        );

        HttpEntity<Map<String,Object>> request = new HttpEntity<>(body);
        restTemplate.put(url,request);

    }

    @Override
    public void upsertPoint(String collectionName, QdrantPoint point) {
        String url = baseUrl+ "/collections/"+collectionName+"/points";

        Map<String , Object> pointData=
                Map.of(
                        "id",point.getId(),
                        "vector",point.getVector(),
                        "payload", point.getPayload()

        );

        Map<String,Object> body = Map.of(
                "points", List.of(pointData)
        );

        HttpEntity<Map<String,Object>> request= new HttpEntity<>(body);
        restTemplate.put(url,request);
    }

    @Override
    public List<QdrantSearchResult> search(
            String collectionName,
            List<Double> queryVector,
            int limit) {

        String url = baseUrl+"/collections/"+collectionName+"/points/search";

        Map<String, Object > body = Map.of(
                "vector", queryVector,
                "limit", limit,
                "with_payload",true
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body);

        ResponseEntity<Map> response=
                restTemplate.postForEntity(url,request,Map.class);

        List<Map<String,Object>> results =
                (List<Map<String,Object>>) response.getBody().get("result");

        if(response.getBody()== null || !response.getBody().containsKey("result")){
            throw new RuntimeException("invalid Qdrant response");
        }

        return results.stream().map(r->{
            QdrantSearchResult res= new QdrantSearchResult();
            res.setId(((Number) r.get("id")).longValue());
            res.setScore(((Number)r.get("score")).doubleValue());
            res.setPayload(((Map<String, Object>) r.get("payload")));
            return res;
        }).toList();
    }
}
