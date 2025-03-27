package org.example.intvincentchan00.Model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class QuestionRequest {
    @NotBlank(message = "Question text cannot be empty")
    private String text;

    private boolean singleChoice;

    @NotEmpty(message = "Question must have at least one answer")
    @Size(min = 1, message = "Each question must have at least one answer")
    private List<AnswerRequest> answers;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSingleChoice() {
        return singleChoice;
    }

    public void setSingleChoice(boolean singleChoice) {
        this.singleChoice = singleChoice;
    }

    public List<AnswerRequest> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerRequest> answers) {
        this.answers = answers;
    }
}
