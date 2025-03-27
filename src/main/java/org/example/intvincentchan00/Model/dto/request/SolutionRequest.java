package org.example.intvincentchan00.Model.dto.request;

import java.util.Map;
import java.util.Set;

public class SolutionRequest {
    private Map<Long, Set<Long>> questionAnswers; // Question ID -> Set of selected Answer IDs

    public Map<Long, Set<Long>> getQuestionAnswers() {
        return questionAnswers;
    }

    public void setQuestionAnswers(Map<Long, Set<Long>> questionAnswers) {
        this.questionAnswers = questionAnswers;
    }
}
