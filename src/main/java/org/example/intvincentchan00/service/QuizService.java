package org.example.intvincentchan00.service;

import org.apache.coyote.BadRequestException;
import org.example.intvincentchan00.Model.dto.request.QuizRequest;
import org.example.intvincentchan00.Model.dto.response.AnswerResponse;
import org.example.intvincentchan00.Model.dto.response.QuestionResponse;
import org.example.intvincentchan00.Model.dto.response.QuizResponse;
import org.example.intvincentchan00.entity.*;
import org.example.intvincentchan00.repository.AnswerRepository;
import org.example.intvincentchan00.repository.QuestionRepository;
import org.example.intvincentchan00.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final AuthService authService;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public QuizService(
            QuizRepository quizRepository,
            QuestionRepository questionRepository,
            AnswerRepository answerRepository,
            AuthService authService) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.authService = authService;
    }

    /**
     * Create a new quiz.
     *
     * @param quizRequest The quiz creation request
     * @return The created quiz response
     */
    @Transactional
    public QuizResponse createQuiz(QuizRequest quizRequest) throws BadRequestException {
        User currentUser = authService.getCurrentUser();

        // Validate quiz data
        if (quizRequest.getTitle() == null || quizRequest.getTitle().isEmpty()) {
            throw new BadRequestException("Quiz title cannot be empty");
        }
        if (quizRequest.getQuestions() == null || quizRequest.getQuestions().isEmpty()) {
            throw new BadRequestException("Quiz must have at least one question");
        }
        if (quizRequest.getQuestions().size() > 10) {
            throw new BadRequestException("Quiz cannot have more than 10 questions");
        }

        // Pre-validate all questions and answers
        for (var questionDto : quizRequest.getQuestions()) {
            if (questionDto.getText() == null || questionDto.getText().isEmpty()) {
                throw new BadRequestException("Question text cannot be empty");
            }

            if (questionDto.getAnswers() == null || questionDto.getAnswers().isEmpty()) {
                throw new BadRequestException("Question must have at least one answer");
            }

            if (questionDto.getAnswers().size() < 1 || questionDto.getAnswers().size() > 5) {
                throw new BadRequestException("Question must have between 1 and 5 possible answers");
            }

            int correctAnswersCount = 0;
            for (var answerDto : questionDto.getAnswers()) {
                if (answerDto.getText() == null || answerDto.getText().isEmpty()) {
                    throw new BadRequestException("Answer text cannot be empty");
                }

                if (answerDto.isCorrect()) {
                    correctAnswersCount++;
                }
            }

            if (questionDto.isSingleChoice() && correctAnswersCount != 1) {
                throw new BadRequestException("Single-choice question must have exactly one correct answer");
            }

            if (!questionDto.isSingleChoice() && correctAnswersCount < 1) {
                throw new BadRequestException("Multiple-choice question must have at least one correct answer");
            }
        }

        // Create quiz entity
        Quiz quiz = new Quiz();
        quiz.setTitle(quizRequest.getTitle());
        quiz.setPublicId(UUID.randomUUID().toString().replace("-",""));
        quiz.setPublished(false);
        quiz.setAuthor(currentUser);

        // Save quiz first to get an ID
        Quiz savedQuiz = quizRepository.saveAndFlush(quiz);

        // Create questions and answers
        for (var questionDto : quizRequest.getQuestions()) {
            Question question = new Question();
            question.setText(questionDto.getText());
            question.setSingleChoice(questionDto.isSingleChoice());
            question.setQuiz(savedQuiz);

            // Save question to get an ID
            Question savedQuestion = questionRepository.saveAndFlush(question);

            // Create and save answers
            for (var answerDto : questionDto.getAnswers()) {
                Answer answer = new Answer();
                answer.setText(answerDto.getText());
                answer.setCorrect(answerDto.isCorrect());
                answer.setQuestion(savedQuestion);
                answerRepository.saveAndFlush(answer);
            }
        }

        // Clear entity manager cache
        entityManager.clear();

        // Fetch the quiz with all relationships
        Quiz completedQuiz = fetchCompleteQuiz(savedQuiz.getId());

        // Convert to response DTO
        return mapQuizToResponse(completedQuiz);
    }

    /**
     * Update an unpublished quiz.
     *
     * @param publicId The public ID of the quiz
     * @param quizRequest The updated quiz data
     * @return The updated quiz response
     */
    @Transactional
    public QuizResponse updateQuiz(String publicId, QuizRequest quizRequest) throws BadRequestException {
        User currentUser = authService.getCurrentUser();

        Quiz quiz = quizRepository.findByPublicId(publicId)
                .orElseThrow(() -> new BadRequestException("Quiz not found"));

        // Validate that the current user is the quiz author
        if (!quiz.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only update your own quizzes");
        }

        // Validate that the quiz is not published
        if (quiz.isPublished()) {
            throw new BadRequestException("Cannot update a published quiz");
        }

        // Validate quiz fields
        if (quizRequest.getTitle() == null || quizRequest.getTitle().isEmpty()) {
            throw new BadRequestException("Quiz title cannot be empty");
        }

        if (quizRequest.getQuestions() == null || quizRequest.getQuestions().isEmpty()) {
            throw new BadRequestException("Quiz must have at least one question");
        }

        if (quizRequest.getQuestions().size() > 10) {
            throw new BadRequestException("Quiz cannot have more than 10 questions");
        }

        // Validate all questions and answers
        for (var questionDto : quizRequest.getQuestions()) {
            if (questionDto.getText() == null || questionDto.getText().isEmpty()) {
                throw new BadRequestException("Question text cannot be empty");
            }

            if (questionDto.getAnswers() == null || questionDto.getAnswers().isEmpty()) {
                throw new BadRequestException("Question must have at least one answer");
            }

            if (questionDto.getAnswers().size() < 1 || questionDto.getAnswers().size() > 5) {
                throw new BadRequestException("Question must have between 1 and 5 possible answers");
            }

            int correctAnswersCount = 0;
            for (var answerDto : questionDto.getAnswers()) {
                if (answerDto.getText() == null || answerDto.getText().isEmpty()) {
                    throw new BadRequestException("Answer text cannot be empty");
                }

                if (answerDto.isCorrect()) {
                    correctAnswersCount++;
                }
            }

            if (questionDto.isSingleChoice() && correctAnswersCount != 1) {
                throw new BadRequestException("Single-choice question must have exactly one correct answer");
            }

            if (!questionDto.isSingleChoice() && correctAnswersCount < 1) {
                throw new BadRequestException("Multiple-choice question must have at least one correct answer");
            }
        }

        // Update quiz title
        quiz.setTitle(quizRequest.getTitle());

        // Clear existing questions (JPA will handle cascading deletes with orphanRemoval)
        quiz.getQuestions().clear();

        // Save first to apply the removal
        quizRepository.saveAndFlush(quiz);

        // Create new questions and answers
        for (var questionDto : quizRequest.getQuestions()) {
            Question question = new Question();
            question.setText(questionDto.getText());
            question.setSingleChoice(questionDto.isSingleChoice());
            question.setQuiz(quiz);

            // Add to quiz (important for maintaining the relationship)
            quiz.getQuestions().add(question);

            // Create answers
            for (var answerDto : questionDto.getAnswers()) {
                Answer answer = new Answer();
                answer.setText(answerDto.getText());
                answer.setCorrect(answerDto.isCorrect());
                answer.setQuestion(question);

                // Add to question (important for maintaining the relationship)
                if (question.getAnswers() == null) {
                    question.setAnswers(new ArrayList<>());
                }
                question.getAnswers().add(answer);
            }
        }

        // Save the quiz with all its new questions and answers
        quiz = quizRepository.saveAndFlush(quiz);

        // Clear persistence context
        entityManager.clear();

        // Reload the quiz
        Quiz refreshedQuiz = quizRepository.findById(quiz.getId())
                .orElseThrow(() -> new BadRequestException("Quiz not found"));

        // Load questions and answers
        for (Question question : refreshedQuiz.getQuestions()) {
            if (question.getAnswers() != null) {
                question.getAnswers().size(); // Force initialization
            }
        }

        return mapQuizToResponse(refreshedQuiz);
    }

    /**
     * Helper method to fetch a complete quiz with all its questions and answers.
     */
    private Quiz fetchCompleteQuiz(Long quizId) throws BadRequestException {
        // Fetch the quiz with its questions
        Quiz quiz = entityManager.createQuery(
                        "SELECT DISTINCT q FROM Quiz q " +
                                "LEFT JOIN FETCH q.questions " +
                                "WHERE q.id = :quizId", Quiz.class)
                .setParameter("quizId", quizId)
                .getSingleResult();

        // Fetch and initialize the answers for each question
        for (Question question : new ArrayList<>(quiz.getQuestions())) {
            // Fetch the answers for this question
            List<Answer> answers = entityManager.createQuery(
                            "SELECT a FROM Answer a WHERE a.question.id = :questionId", Answer.class)
                    .setParameter("questionId", question.getId())
                    .getResultList();

            question.getAnswers().clear();
            question.getAnswers().addAll(answers);
        }

        return quiz;
    }

    /**
     * Get a quiz by its public ID.
     *
     * @param publicId The public ID of the quiz
     * @return The quiz response
     */
    @Transactional(readOnly = true)
    public QuizResponse getQuizByPublicId(String publicId) throws BadRequestException {
        Quiz quiz = quizRepository.findByPublicId(publicId)
                .orElseThrow(() -> new BadRequestException("Quiz not found"));

        // If quiz is not published, only the author can view it
        if (!quiz.isPublished()) {
            User currentUser = authService.getCurrentUser();
            if (!quiz.getAuthor().getId().equals(currentUser.getId())) {
                throw new BadRequestException("Quiz is not published yet");
            }
        }

        // Fetch the complete quiz with all relationships
        Quiz completedQuiz = fetchCompleteQuiz(quiz.getId());

        return mapQuizToResponse(completedQuiz);
    }

    /**
     * Get all published quizzes.
     *
     * @return List of published quiz responses
     */
    @Transactional(readOnly = true)
    public List<QuizResponse> getAllPublishedQuizzes() {
        List<Quiz> quizzes = quizRepository.findAllByPublishedIsTrue();

        // For each quiz, load its questions and answers
        List<QuizResponse> responses = new ArrayList<>();
        for (Quiz quiz : quizzes) {
            try {
                Quiz completedQuiz = fetchCompleteQuiz(quiz.getId());
                responses.add(mapQuizToResponse(completedQuiz));
            } catch (BadRequestException e) {
                // Skip this quiz if there's an error
            }
        }

        return responses;
    }

    /**
     * Get all quizzes created by the current user.
     *
     * @return List of quiz responses
     */
    @Transactional(readOnly = true)
    public List<QuizResponse> getMyQuizzes() throws BadRequestException {
        User currentUser = authService.getCurrentUser();
        List<Quiz> quizzes = quizRepository.findByAuthorId(currentUser.getId());

        // For each quiz, load its questions and answers
        List<QuizResponse> responses = new ArrayList<>();
        for (Quiz quiz : quizzes) {
            Quiz completedQuiz = fetchCompleteQuiz(quiz.getId());
            responses.add(mapQuizToResponse(completedQuiz));
        }

        return responses;
    }

    /**
     * Publish a quiz.
     *
     * @param publicId The public ID of the quiz
     * @return The published quiz response
     */
    @Transactional
    public QuizResponse publishQuiz(String publicId) throws BadRequestException {
        User currentUser = authService.getCurrentUser();

        Quiz quiz = quizRepository.findByPublicId(publicId)
                .orElseThrow(() -> new BadRequestException("Quiz not found"));

        // Validate that the current user is the quiz author
        if (!quiz.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only publish your own quizzes");
        }

        // Validate that the quiz is not already published
        if (quiz.isPublished()) {
            throw new BadRequestException("Quiz is already published");
        }

        // Validate that the quiz has questions
        if (quiz.getQuestions().isEmpty()) {
            throw new BadRequestException("Cannot publish a quiz without questions");
        }

        quiz.setPublished(true);
        Quiz savedQuiz = quizRepository.save(quiz);

        // Fetch the complete quiz with all relationships
        Quiz completedQuiz = fetchCompleteQuiz(savedQuiz.getId());

        return mapQuizToResponse(completedQuiz);
    }

    /**
     * Delete a quiz.
     *
     * @param publicId The public ID of the quiz
     */
    @Transactional
    public void deleteQuiz(String publicId) throws BadRequestException {
        User currentUser = authService.getCurrentUser();

        Quiz quiz = quizRepository.findByPublicId(publicId)
                .orElseThrow(() -> new BadRequestException("Quiz not found"));

        // Validate that the current user is the quiz author
        if (!quiz.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only delete your own quizzes");
        }

        quizRepository.delete(quiz);
    }

    /**
     * Map Quiz entity to QuizResponse DTO.
     *
     * @param quiz The quiz entity
     * @return The quiz response DTO
     */
    private QuizResponse mapQuizToResponse(Quiz quiz) {
        QuizResponse response = new QuizResponse();
        response.setId(quiz.getId());
        response.setPublicId(quiz.getPublicId());
        response.setTitle(quiz.getTitle());
        response.setPublished(quiz.isPublished());
        response.setAuthorId(quiz.getAuthor().getId());
        response.setAuthorName(quiz.getAuthor().getName());

        // Map questions and answers
        List<QuestionResponse> questionResponses = new ArrayList<>();

        if (quiz.getQuestions() != null) {
            for (Question question : quiz.getQuestions()) {
                QuestionResponse questionResponse = new QuestionResponse();
                questionResponse.setId(question.getId());
                questionResponse.setText(question.getText());
                questionResponse.setSingleChoice(question.isSingleChoice());

                List<AnswerResponse> answerResponses = new ArrayList<>();

                if (question.getAnswers() != null) {
                    // Get current user to check if they are the author
                    User currentUser = null;
                    boolean isAuthor = false;
                    try {
                        currentUser = authService.getCurrentUser();
                        isAuthor = quiz.getAuthor().getId().equals(currentUser.getId());
                    } catch (Exception e) {
                        // If we can't get the current user, assume they're not the author
                    }

                    for (Answer answer : question.getAnswers()) {
                        AnswerResponse answerResponse = new AnswerResponse();
                        answerResponse.setId(answer.getId());
                        answerResponse.setText(answer.getText());

                        // Only show if answer is correct to the quiz author
                        if (isAuthor) {
                            answerResponse.setCorrect(answer.isCorrect());
                        }

                        answerResponses.add(answerResponse);
                    }
                }

                questionResponse.setAnswers(answerResponses);
                questionResponses.add(questionResponse);
            }
        }

        response.setQuestions(questionResponses);
        return response;
    }
}