package com.faizan.taskmanager.Controller;

import com.faizan.taskmanager.Entity.Task;
import com.faizan.taskmanager.Repository.TaskRepository;
import com.faizan.taskmanager.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    public String cleanResponse(String response) {
        return response
                .replaceAll("\\n", " ")
                .replaceAll("(?i)task description:", "")
                .replaceAll("(?i)task:", "")   // ⭐ NEW
                .trim();
    }

    @GetMapping("/test-ai")
    public String testAI() {

        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:11434/api/chat";

        Map<String, Object> request = new HashMap<>();
        request.put("model", "tinyllama");
        request.put("stream", false);

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", """
You are a task assistant.

Write ONLY a short, clear, one-line description of the task.

Do NOT repeat the task name.
Do NOT write "Task:".
Do NOT add labels.
Do NOT explain.

Task: buying groceries
""");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(message);

        request.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                url,
                entity,
                Map.class
        );

        Map body = response.getBody();
        Map messageMap = (Map) body.get("message");

        String result = (String) messageMap.get("content");
        return cleanResponse(result);
    }

    @GetMapping("/api/test")
    public String testApi(){
        return "EveryThing working";
    }

    @PostMapping("/api/post")
    public Task postTask(@RequestBody Task task){
        return taskService.createTask(task);
    }

    @GetMapping("/api/get")
    public List<Task> getAll(){
        return taskRepository.findAll();
    }

}
