package com.faizan.taskmanager.Service;

import org.springframework.stereotype.Service;

@Service
public class QueryRouterService {

    private final KnowledgeQueryService fastService;
    private final TaskQueryService slowService;

    public QueryRouterService(KnowledgeQueryService fastService,
                              TaskQueryService slowService) {
        this.fastService = fastService;
        this.slowService = slowService;
    }

    public String route(String query) throws InterruptedException {

        // default → fast path
        return fastService.answerQueryFast(query);

        // later you can add:
        // if special query → use slowService
    }
}
