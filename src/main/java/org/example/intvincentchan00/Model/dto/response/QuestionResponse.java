package org.example.intvincentchan00.Model.dto.response;

import org.example.intvincentchan00.entity.Question;

import java.util.List;

public class QuestionResponse {
    private Long id;
    private String text;
    private boolean singleChoice;
    private List<AnswerResponse> answers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<AnswerResponse> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerResponse> answers) {
        this.answers = answers;
    }

}
