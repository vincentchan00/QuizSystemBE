package org.example.intvincentchan00.service;

import org.apache.coyote.BadRequestException;
import org.example.intvincentchan00.Model.dto.request.QuestionRequest;
import org.example.intvincentchan00.Model.dto.response.AnswerResponse;
import org.example.intvincentchan00.Model.dto.response.QuestionResponse;
import org.example.intvincentchan00.entity.Answer;
import org.example.intvincentchan00.entity.Question;
import org.example.intvincentchan00.entity.Quiz;
import org.example.intvincentchan00.entity.User;
import org.example.intvincentchan00.repository.QuestionRepository;
import org.example.intvincentchan00.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing questions within quizzes.
 */
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final AuthService authService;

    @Autowired
    public QuestionService(
            QuestionRepository questionRepository,
            QuizRepository quizRepository,
            AuthService authService) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.authService = authService;
    }

    /**
     * Get a question by ID.
     *
     * @param questionId The ID of the question
     * @return The question response
     */
    public QuestionResponse getQuestion(Long questionId) throws BadRequestException {
        User currentUser = authService.getCurrentUser();

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BadRequestException("Question not found"));

        Quiz quiz = question.getQuiz();

        // If quiz is not published, only the author can view it
        if (!quiz.isPublished() && !quiz.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Question belongs to an unpublished quiz");
        }

        return mapQuestionToResponse(question);
    }

    /**
     * Map Question entity to QuestionResponse DTO.
     * @param question The question entity
     * @return The question response DTO
     */
    private QuestionResponse mapQuestionToResponse(Question question) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setText(question.getText());
        response.setSingleChoice(question.isSingleChoice());

        // Check if current user is quiz author to determine whether to include correct answers
        User currentUser = null;
        boolean isAuthor = false;

        try {
            currentUser = authService.getCurrentUser();
            isAuthor = question.getQuiz().getAuthor().getId().equals(currentUser.getId());
        } catch (Exception e) {
            // If we can't get the current user, assume they're not the author
        }

        // Map answers
        boolean finalIsAuthor = isAuthor;
        response.setAnswers(
                question.getAnswers().stream().map(answer -> {
                    AnswerResponse answerResponse = new AnswerResponse();
                    answerResponse.setId(answer.getId());
                    answerResponse.setText(answer.getText());

                    // Only include correct flag for quiz authors
                    if (finalIsAuthor) {
                        answerResponse.setCorrect(answer.isCorrect());
                    }

                    return answerResponse;
                }).collect(Collectors.toList())
        );

        return response;
    }
}