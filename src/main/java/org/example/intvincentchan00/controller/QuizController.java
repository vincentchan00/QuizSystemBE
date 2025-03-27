package org.example.intvincentchan00.controller;

import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.example.intvincentchan00.Model.dto.request.QuizRequest;
import org.example.intvincentchan00.Model.dto.response.QuizResponse;
import org.example.intvincentchan00.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * Get all published quizzes
     * @return all published quizzes
     */
    @GetMapping()
    public ResponseEntity<?> getQuizzes() {
        return ResponseEntity.status(HttpStatus.OK).body(quizService.getAllPublishedQuizzes());
    }

    /**
     * Get specific quiz
     * @return specific quiz
     */
    @GetMapping("{publicId}")
    public ResponseEntity<QuizResponse> getQuizByPublicId(@PathVariable String publicId) throws BadRequestException {
        QuizResponse quizResponse = quizService.getQuizByPublicId(publicId);
        return ResponseEntity.ok(quizResponse);
    }

    @PostMapping
    public ResponseEntity<QuizResponse> createQuiz(@Valid @RequestBody QuizRequest quizRequest) throws BadRequestException {
        QuizResponse quizResponse = quizService.createQuiz(quizRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(quizResponse);
    }

    /**
     * Get all quizzes created by the current user.
     * @return List of quizzes created by the current user
     */
    @GetMapping("/mine")
    public ResponseEntity<?> getMyQuizzes() throws BadRequestException {
        List<QuizResponse> myQuizzes = quizService.getMyQuizzes();
        return ResponseEntity.ok(myQuizzes);
    }

    /**
     * Update a quiz by public ID.
     * @param publicId     The public ID of the quiz
     * @param quizRequest  The updated quiz data
     * @return The updated quiz response
     */
    @PutMapping("/{publicId}")
    public ResponseEntity<QuizResponse> updateQuiz(
            @PathVariable String publicId,
            @Valid @RequestBody QuizRequest quizRequest
    ) throws BadRequestException {
        QuizResponse updatedQuiz = quizService.updateQuiz(publicId, quizRequest);
        return ResponseEntity.ok(updatedQuiz);
    }

    /**
     * Publish a quiz by public ID
     * @param publicId The public ID of the quiz
     * @return The published quiz response
     */
    @PutMapping("/{publicId}/publish")
    public ResponseEntity<QuizResponse> publishQuiz(@PathVariable String publicId) throws BadRequestException {
        QuizResponse publishedQuiz = quizService.publishQuiz(publicId);
        return ResponseEntity.ok(publishedQuiz);
    }

    /**
     * Delete a quiz by public ID
     * @param publicId The public ID of the quiz
     * @return ResponseEntity with status 204 No Content
     */
    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable String publicId) throws BadRequestException {
        quizService.deleteQuiz(publicId);
        return ResponseEntity.noContent().build();
    }
}
