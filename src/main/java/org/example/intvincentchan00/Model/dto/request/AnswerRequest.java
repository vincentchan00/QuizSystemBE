package org.example.intvincentchan00.Model.dto.request;

import jakarta.validation.constraints.NotBlank;

public class AnswerRequest {
    @NotBlank(message = "Answer text cannot be empty")
    private String text;

    private boolean correct;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
