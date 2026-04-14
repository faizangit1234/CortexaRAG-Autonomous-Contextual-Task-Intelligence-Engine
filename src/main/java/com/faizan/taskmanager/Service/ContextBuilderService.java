package com.faizan.taskmanager.Service;

import com.faizan.taskmanager.Entity.ScoredEntry;
import com.faizan.taskmanager.Entity.VectorEntry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContextBuilderService {

    public String buildContext(List<ScoredEntry> entries){
        StringBuilder context = new StringBuilder();
        context.append("[CONTEXT]\n\n");

        int index =1;

        for(ScoredEntry entry : entries){
            VectorEntry ve = entry.getEntry();

            context.append(index++)
                    .append(". Task: ")
                    .append(ve.getMetadata().getTitle())
                    .append("\n");

            context.append("  Description: ")
                    .append(ve.getMetadata().getDescription())
                    .append("\n\n");
        }

        return context.toString();
    }
}
