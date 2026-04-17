package com.faizan.taskmanager.Service;

import com.faizan.taskmanager.Constant.ConfidenceLevel;
import com.faizan.taskmanager.Entity.ScoredEntry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConfidenceEvaluator {

    public ConfidenceLevel evaluate(List<ScoredEntry> results){
        if(results == null || results.isEmpty()){
            return ConfidenceLevel.LOW;
        }

        double topScore = results.get(0).getScore();
        double secondScore = results.size() > 1 ? results.get(1).getScore() : 0.0;
        double gap = topScore - secondScore;

        if(topScore >= 0.85 && gap >= 0.10){
            return ConfidenceLevel.HIGH;
        }

        if(topScore >= 0.70){
            return ConfidenceLevel.MEDIUM;
        }

        return ConfidenceLevel.LOW;
    }
}
