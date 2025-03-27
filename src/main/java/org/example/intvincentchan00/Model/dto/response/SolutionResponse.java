package org.example.intvincentchan00.Model.dto.response;

import java.util.List;

public class SolutionResponse {
    private Long id;
    private String publicId;
    private Long quizId;
    private String quizPublicId;
    private String quizTitle;
    private Long userId;
    private String userName;
    private double totalScore;
    private double scorePercentage;
    private String submittedAt;
    private List<QuestionSolutionResponse> questionSolutions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getQuizPublicId() {
        return quizPublicId;
    }

    public void setQuizPublicId(String quizPublicId) {
        this.quizPublicId = quizPublicId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public double getScorePercentage() {
        return scorePercentage;
    }

    public void setScorePercentage(double scorePercentage) {
        this.scorePercentage = scorePercentage;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    public List<QuestionSolutionResponse> getQuestionSolutions() {
        return questionSolutions;
    }

    public void setQuestionSolutions(List<QuestionSolutionResponse> questionSolutions) {
        this.questionSolutions = questionSolutions;
    }
}
