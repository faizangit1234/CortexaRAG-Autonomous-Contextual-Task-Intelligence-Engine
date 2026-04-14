package com.faizan.taskmanager.Controller;


import com.faizan.taskmanager.Dto.EmbeddingRequest;
import com.faizan.taskmanager.Service.EmbeddingService;
import com.faizan.taskmanager.Service.TaskQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private TaskQueryService taskQueryService;


    @GetMapping("/embed")
    public List<Double> embed(@RequestBody EmbeddingRequest request){
        return embeddingService.embedText(request);
    }

    @GetMapping("/query")
    public String query(@RequestParam String q){
        return taskQueryService.answerQuery(q);
    }

}
