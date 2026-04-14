//package com.faizan.taskmanager.Client;
//
//import com.faizan.taskmanager.Dto.QdrantPoint;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class QdrantTestRunner implements CommandLineRunner {
//
//    private final QdrantClient qdrantClient;
//
//    public QdrantTestRunner(QdrantClient qdrantClient) {
//        this.qdrantClient = qdrantClient;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//
//        String collection = "tasks";
//
//        // 1. Create collection
//        qdrantClient.createCollection(collection, 768);
//
//        // 2. Insert vector
//        QdrantPoint point = new QdrantPoint();
//        point.setId(1L);
//        point.setVector(generateDummyVector(768));
//        point.setPayload(Map.of(
//                "title", "Fix login bug",
//                "description", "Resolve authentication issue"
//        ));
//
//        qdrantClient.upsertPoint(collection, point);
//
//        // 3. Search
//        List<Double> queryVector = generateDummyVector(768);
//
//        var results = qdrantClient.search(collection, queryVector, 2);
//
//        // 4. Print raw output
//        System.out.println("=== SEARCH RESULTS ===");
//        results.forEach(r -> {
//            System.out.println("ID: " + r.getId());
//            System.out.println("Score: " + r.getScore());
//            System.out.println("Payload: " + r.getPayload());
//            System.out.println("----------------------");
//        });
//    }
//
//    private List<Double> generateDummyVector(int size) {
//        List<Double> vector = new ArrayList<>();
//        for (int i = 0; i < size; i++) {
//            vector.add(Math.random());
//        }
//        return vector;
//    }
//}