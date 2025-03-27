package org.example.intvincentchan00.Model.dto.response;

import java.util.Set;

public class QuestionSolutionResponse {
    private Long questionId;
    private String questionText;
    private double score;
    private Set<Long> selectedAnswerIds;

    public QuestionSolutionResponse(Long questionId, double score, Set<Long> selectedAnswerIds) {
        this.questionId = questionId;
        this.score = score;
        this.selectedAnswerIds = selectedAnswerIds;
    }

    public QuestionSolutionResponse() {

    }

    // Getters and setters
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Set<Long> getSelectedAnswerIds() {
        return selectedAnswerIds;
    }

    public void setSelectedAnswerIds(Set<Long> selectedAnswerIds) {
        this.selectedAnswerIds = selectedAnswerIds;
    }
}
