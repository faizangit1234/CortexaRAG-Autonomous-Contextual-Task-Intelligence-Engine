package com.faizan.taskmanager.Service;

import com.faizan.taskmanager.Dto.EmbeddingRequest;
import com.faizan.taskmanager.Entity.MetaData;
import com.faizan.taskmanager.Entity.Task;
import com.faizan.taskmanager.Entity.VectorEntry;
import com.faizan.taskmanager.Repository.TaskRepository;
import com.faizan.taskmanager.Repository.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final AiService aiService;
    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;

    public TaskService(TaskRepository taskRepository, AiService aiService, VectorStore vectorStore, EmbeddingService embeddingService) {
        this.taskRepository = taskRepository;
        this.aiService = aiService;
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
    }

//    public Task saveTask(Task task){
//        if(task.getDescription()== null || task.getDescription().isBlank()){
//            try{
//
//            String aiDescription= aiService.generateTaskDescription(task.getTitle());
//            task.setDescription(aiDescription);
//            }catch (Exception e){
//                task.setDescription(task.getTitle());
//            }
//        }
//        return taskRepository.save(task);
//    }

    public Task createTask(Task task){

        // 1. Generate description if missing
        if(task.getDescription() == null || task.getDescription().isBlank()){
            try{
                String aiDescription = aiService.generateTaskDescription(task.getTitle());
                task.setDescription(aiDescription);
            } catch (Exception e){
                task.setDescription(task.getTitle());
            }
        }

        // 2. Save to DB FIRST (important for ID)
        Task savedTask = taskRepository.save(task);

        // 3. Generate embedding
        try{

        List<Double> vector =
                embeddingService.embedText(
                        new EmbeddingRequest(savedTask.getDescription())
                );

        //create new meta data object
            MetaData metaData = new MetaData(savedTask.getTitle(),
                    savedTask.getDescription());

        // 4. Store in vector store
        vectorStore.save(
                new VectorEntry(
                        savedTask.getId(),
                        vector,
                        metaData
                )
        );

            System.out.println("=== VECTOR STORED ===");
            System.out.println("Task ID: " + savedTask.getId());
            System.out.println("Description: " + savedTask.getDescription());
            System.out.println("Vector size: " + vector.size());

        }catch (Exception e){
            // Log but don't fail task creation
            System.out.println("Embedding failed: " + e.getMessage());
        }


        return savedTask;
    }
}
