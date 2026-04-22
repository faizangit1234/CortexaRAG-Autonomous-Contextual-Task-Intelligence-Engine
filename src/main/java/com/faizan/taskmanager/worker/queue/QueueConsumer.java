package com.faizan.taskmanager.worker.queue;

import com.faizan.taskmanager.worker.model.LLMJob;

public interface QueueConsumer {
    LLMJob consume();
}
