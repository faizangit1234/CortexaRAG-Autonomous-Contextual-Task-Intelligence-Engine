package com.faizan.taskmanager.worker;

import com.faizan.taskmanager.worker.model.LLMJob;
import com.faizan.taskmanager.worker.processor.JobProcessor;
import com.faizan.taskmanager.worker.queue.QueueConsumer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkerRunner {
    private final QueueConsumer queueConsumer;
    private final JobProcessor jobProcessor;


    @PostConstruct
    public void start(){
        log.info("Worker thread starting...");
        new Thread(this::run).start();
    }

    public void run(){
        log.info("Worker Started ...");

        while(true){
            try {
                LLMJob job = queueConsumer.consume();

                if (job == null) continue;

                log.info("Processing job: {}", job.getQuery());

                jobProcessor.process(job);

            } catch (Exception e) {
                log.error("Worker error", e);
                // DO NOT crash thread
            }

        }
    }
}
