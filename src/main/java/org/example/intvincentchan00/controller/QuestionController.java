package org.example.intvincentchan00.controller;

import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.example.intvincentchan00.Model.dto.request.QuestionRequest;
import org.example.intvincentchan00.Model.dto.request.QuizRequest;
import org.example.intvincentchan00.Model.dto.response.QuestionResponse;
import org.example.intvincentchan00.Model.dto.response.QuizResponse;
import org.example.intvincentchan00.service.QuestionService;
import org.example.intvincentchan00.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }
    /**
     * Get the Question by questionId
     * @param questionId The question ID
     * @return Question information
     */
    @GetMapping("{questionId}")
    public ResponseEntity<?> getQuestion(@Valid @PathVariable Long questionId) {
        try {
            QuestionResponse questionResponse = questionService.getQuestion(questionId);
            return ResponseEntity.status(HttpStatus.OK).body(questionResponse);
        } catch (BadRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

}
