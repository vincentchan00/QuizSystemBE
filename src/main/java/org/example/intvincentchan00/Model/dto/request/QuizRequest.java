package org.example.intvincentchan00.Model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class QuizRequest {
    @NotBlank(message = "Quiz title cannot be empty")
    private String title;

    @NotEmpty(message = "Quiz must have at least one question")
    private List<QuestionRequest> questions = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<QuestionRequest> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionRequest> questions) {
        this.questions = questions;
    }
}
