package com.faizan.taskmanager.Controller;

import com.faizan.taskmanager.Entity.VectorEntry;
import com.faizan.taskmanager.Service.InMemoryVectorStore;
import com.faizan.taskmanager.Repository.VectorStore;
import com.faizan.taskmanager.Service.RetrieverService;
import com.faizan.taskmanager.Util.VectorUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vectors")
public class VectorController {

    private final VectorStore vectorStore;
    private final RetrieverService retrieverService;

    public VectorController(VectorStore vectorStore ,RetrieverService retrieverService) {
        this.vectorStore = vectorStore;
        this.retrieverService= retrieverService;
    }

    @GetMapping
    public List<VectorEntry> getAllVectors() {
        return vectorStore.findAll();
    }

    @GetMapping("/count")
    public int count() {
        return ((InMemoryVectorStore) vectorStore).size();
    }

    @GetMapping("/test-similarity")
    public void testSimilarity() {

        List<VectorEntry> all = vectorStore.findAll();

        if (all.size() < 2) {
            System.out.println("Not enough data");
            return;
        }

        double score = VectorUtils.cosineSimilarity(
                all.get(0).getVector(),
                all.get(1).getVector()
        );

        System.out.println("Similarity: " + score);
    }

    @GetMapping("/search")
    public List<VectorEntry> search(@RequestParam String query) {
        return retrieverService.search(query, 3);
    }
}
