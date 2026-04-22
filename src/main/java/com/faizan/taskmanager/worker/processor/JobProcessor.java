package com.faizan.taskmanager.worker.processor;

import com.faizan.taskmanager.worker.model.LLMJob;

public interface JobProcessor {
    void process(LLMJob job);
}
