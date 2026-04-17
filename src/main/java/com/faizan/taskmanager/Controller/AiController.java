package com.faizan.taskmanager.Controller;


import com.faizan.taskmanager.Dto.EmbeddingRequest;
import com.faizan.taskmanager.Service.EmbeddingService;
import com.faizan.taskmanager.Service.QueryRouterService;
import com.faizan.taskmanager.Service.TaskQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final EmbeddingService embeddingService;

    //might need later
//    private final TaskQueryService taskQueryService;

    private final QueryRouterService queryRouterService;




    @GetMapping("/embed")
    public List<Double> embed(@RequestBody EmbeddingRequest request){
        return embeddingService.embedText(request);
    }

    @GetMapping("/query")
    public String query(@RequestParam String q) throws InterruptedException {
        return queryRouterService.route(q);
    }

}
