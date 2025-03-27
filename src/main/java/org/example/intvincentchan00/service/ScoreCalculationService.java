package org.example.intvincentchan00.service;

import org.example.intvincentchan00.entity.Answer;
import org.example.intvincentchan00.entity.Question;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for calculating quiz scores
 */
@Service
public class ScoreCalculationService {

    /**
     * Calculate the score for a single question based on the selected answers.
     * @param question The question
     * @param selectedAnswers The selected answers
     * @return The score for the question
     */
    public double calculateQuestionScore(Question question, Set<Answer> selectedAnswers) {
        // If no answers selected, score is 0
        if (selectedAnswers.isEmpty()) {
            return 0.0;
        }

        if (question.isSingleChoice()) {
            return calculateSingleChoiceScore(question, selectedAnswers);
        } else {
            return calculateMultipleChoiceScore(question, selectedAnswers);
        }
    }

    /**
     * Calculate score for single-choice question.
     * @param question The question
     * @param selectedAnswers The selected answers
     * @return score
     */
    private double calculateSingleChoiceScore(Question question, Set<Answer> selectedAnswers) {
        // user should select only one answer
        if (selectedAnswers.size() != 1) {
            return -1.0;
        }

        Answer selectedAnswer = selectedAnswers.iterator().next();
        return selectedAnswer.isCorrect() ? 1.0 : -1.0;
    }

    /**
     * Calculate score for multiple-choice question.
     * @param question The question
     * @param selectedAnswers The selected answers
     * @return The score from weighted scoring
     */
    private double calculateMultipleChoiceScore(Question question, Set<Answer> selectedAnswers) {
        Set<Answer> correctAnswers = question.getAnswers().stream()
                .filter(Answer::isCorrect)
                .collect(Collectors.toSet());

        Set<Answer> incorrectAnswers = question.getAnswers().stream()
                .filter(answer -> !answer.isCorrect())
                .collect(Collectors.toSet());

        // Calculate correct answers weight
        double correctWeight = 1.0 / correctAnswers.size();
        double incorrectWeight = incorrectAnswers.isEmpty() ? 0 : 1.0 / incorrectAnswers.size();
        double score = 0.0;

        // Add score for correct answer
        for (Answer answer : selectedAnswers) {
            if (answer.isCorrect()) {
                score += correctWeight;
            } else {
                score -= incorrectWeight;
            }
        }

        // Check score is at least -1 and at most 1
        return Math.max(-1.0, Math.min(1.0, score));
    }
}