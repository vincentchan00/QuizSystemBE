package org.example.intvincentchan00.service;


import org.apache.coyote.BadRequestException;
import org.example.intvincentchan00.Model.dto.request.SolutionRequest;
import org.example.intvincentchan00.Model.dto.response.QuestionSolutionResponse;
import org.example.intvincentchan00.Model.dto.response.SolutionResponse;
import org.example.intvincentchan00.entity.*;
import org.example.intvincentchan00.repository.QuestionSolutionRepository;
import org.example.intvincentchan00.repository.QuizRepository;
import org.example.intvincentchan00.repository.SolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SolutionService {

    private final SolutionRepository solutionRepository;
    private final QuizRepository quizRepository;
    private final AuthService authService;
    private final ScoreCalculationService scoreCalculationService;
    private final QuestionSolutionRepository questionSolutionRepository;

    @Autowired
    public SolutionService(
            SolutionRepository solutionRepository,
            QuizRepository quizRepository,
            AuthService authService,
            ScoreCalculationService scoreCalculationService, QuestionSolutionRepository questionSolutionRepository) {
        this.solutionRepository = solutionRepository;
        this.quizRepository = quizRepository;
        this.authService = authService;
        this.scoreCalculationService = scoreCalculationService;
        this.questionSolutionRepository = questionSolutionRepository;
    }

    /**
     * Submit a solution for a quiz.
     *
     * @param publicId The public ID of the quiz
     * @param solutionRequest The solution data
     * @return The solution response with scores
     */
    @Transactional
    public SolutionResponse submitSolution(String publicId, SolutionRequest solutionRequest) throws BadRequestException {
        User currentUser = authService.getCurrentUser();

        Quiz quiz = quizRepository.findByPublicId(publicId)
                .orElseThrow(() -> new BadRequestException("Quiz not found"));

        // Validation
        if (!quiz.isPublished()) {
            throw new BadRequestException("Cannot submit a solution for an unpublished quiz");
        }

        if (solutionRepository.existsByUserIdAndQuizId(currentUser.getId(), quiz.getId())) {
            throw new BadRequestException("You have already submitted a solution for this quiz");
        }

        Map<Long, Set<Long>> questionAnswers = solutionRequest.getQuestionAnswers();

        Map<Long, Question> questionMap = quiz.getQuestions().stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        for (Long questionId : questionAnswers.keySet()) {
            if (!questionMap.containsKey(questionId)) {
                throw new BadRequestException("Invalid question ID: " + questionId);
            }
        }

        for (Question question : quiz.getQuestions()) {
            if (!questionAnswers.containsKey(question.getId())) {
                questionAnswers.put(question.getId(), new HashSet<>());
            }
        }

        // Create solution entity
        Solution solution = new Solution();
        solution.setPublicId(UUID.randomUUID().toString().replace("-",""));
        solution.setUser(currentUser);
        solution.setQuiz(quiz);

        Solution savedSolution = solutionRepository.save(solution);

        List<QuestionSolution> questionSolutions = new ArrayList<>();
        double totalScore = 0;

        for (Question question : quiz.getQuestions()) {
            Set<Long> selectedAnswerIds = questionAnswers.getOrDefault(question.getId(), new HashSet<>());

            // ensure only one answer is selected
            if (question.isSingleChoice() && selectedAnswerIds.size() > 1) {
                throw new BadRequestException(
                        "Question " + question.getId() + " is single-choice but multiple answers were selected");
            }

            // Convert to map
            Map<Long, Answer> answerMap = question.getAnswers().stream()
                    .collect(Collectors.toMap(Answer::getId, a -> a));

            // Validate that selected answers exist for this question
            for (Long answerId : selectedAnswerIds) {
                if (!answerMap.containsKey(answerId)) {
                    throw new BadRequestException(
                            "Invalid answer ID " + answerId + " for question " + question.getId());
                }
            }

            // Calculate score for this question
            double score = scoreCalculationService.calculateQuestionScore(
                    question,
                    selectedAnswerIds.stream()
                            .map(answerMap::get)
                            .collect(Collectors.toSet())
            );

            // Create question solution
            QuestionSolution questionSolution = new QuestionSolution();
            questionSolution.setSolution(savedSolution);
            questionSolution.setQuestion(question);
            questionSolution.setScore(score);
            questionSolution.setSelectedAnswers(
                    selectedAnswerIds.stream()
                            .map(answerMap::get)
                            .collect(Collectors.toSet())
            );

            questionSolutions.add(questionSolution);
            questionSolutionRepository.save(questionSolution);
            totalScore += score;
        }

        // Calculate overall score percentage
        double totalPossibleScore = quiz.getQuestions().size();
        double scorePercentage = (totalScore / totalPossibleScore) * 100;
        scorePercentage = Math.max(0, Math.min(100, scorePercentage));

        savedSolution.setQuestionSolutions(questionSolutions);
        savedSolution.setTotalScore(totalScore);
        savedSolution.setScorePercentage(scorePercentage);

        // Update final details
        savedSolution = solutionRepository.save(savedSolution);

        return mapSolutionToResponse(savedSolution);
    }

    /**
     * Get a solution by its public ID.
     * @param publicId The public ID of the solution
     * @return The solution response
     */
    public SolutionResponse getSolutionByPublicId(String publicId) throws BadRequestException {
        User currentUser = authService.getCurrentUser();

        Solution solution = solutionRepository.findByPublicId(publicId)
                .orElseThrow(() -> new BadRequestException("Solution not found"));

        // Validate that the current user is either the quiz author or the solution submitter
        if (!solution.getUser().getId().equals(currentUser.getId()) &&
                !solution.getQuiz().getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only view your own solutions or solutions to your quizzes");
        }

        return mapSolutionToResponse(solution);
    }

    /**
     * Get all solutions submitted by the current user
     * @return List of solution responses
     */
    public List<SolutionResponse> getMySubmittedSolutions() throws BadRequestException {
        User currentUser = authService.getCurrentUser();

        List<Solution> solutions = solutionRepository.findByUserId(currentUser.getId());
        return solutions.stream()
                .map(this::mapSolutionToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all solutions submitted for a specific quiz
     * @param publicId The public ID of the quiz
     * @return Page of solution responses
     */
    public List<SolutionResponse> getSolutionsForQuiz(String publicId) throws BadRequestException {
        User currentUser = authService.getCurrentUser();

        Quiz quiz = quizRepository.findByPublicId(publicId)
                .orElseThrow(() -> new BadRequestException("Quiz not found"));

        // Validate that the current user is the quiz author
        if (!quiz.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only view solutions for your own quizzes");
        }

        List<Solution> solutions = solutionRepository.findByQuizId(quiz.getId());
        return solutions.stream()
                .map(this::mapSolutionToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Map Solution entity to SolutionResponse DTO.
     *
     * @param solution The solution entity
     * @return The solution response DTO
     */
    private SolutionResponse mapSolutionToResponse(Solution solution) {
        SolutionResponse response = new SolutionResponse();
        response.setId(solution.getId());
        response.setPublicId(solution.getPublicId());
        response.setQuizId(solution.getQuiz().getId());
        response.setQuizPublicId(String.valueOf(solution.getQuiz().getPublicId()));
        response.setQuizTitle(solution.getQuiz().getTitle());
        response.setUserId(solution.getUser().getId());
        response.setUserName(solution.getUser().getName());
        response.setTotalScore(solution.getTotalScore());
        response.setScorePercentage(solution.getScorePercentage());
        response.setSubmittedAt(String.valueOf(solution.getCreatedAt()));

        // Map question solutions
        response.setQuestionSolutions(
                solution.getQuestionSolutions().stream().map(qs -> {
                    QuestionSolutionResponse questionSolution =
                            new QuestionSolutionResponse();
                    questionSolution.setQuestionId(qs.getQuestion().getId());
                    questionSolution.setQuestionText(qs.getQuestion().getText());
                    questionSolution.setScore(qs.getScore());

                    // Include selected answer IDs but not the correctness information
                    questionSolution.setSelectedAnswerIds(
                            qs.getSelectedAnswers().stream()
                                    .map(Answer::getId)
                                    .collect(Collectors.toSet())
                    );

                    return questionSolution;
                }).collect(Collectors.toList())
        );

        return response;
    }
}