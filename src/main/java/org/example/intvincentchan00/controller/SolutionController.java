package org.example.intvincentchan00.controller;

import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.example.intvincentchan00.Model.dto.request.SolutionRequest;
import org.example.intvincentchan00.Model.dto.response.SolutionResponse;
import org.example.intvincentchan00.service.SolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solution")
public class SolutionController {

    private final SolutionService solutionService;

    @Autowired
    public SolutionController(SolutionService solutionService) {
        this.solutionService = solutionService;
    }

    /**
     * Get Solution by public ID
     *
     * @param publicId The public ID of the quiz
     * @return The published quiz solution response
     */
    @GetMapping("{publicId}")
    public ResponseEntity<?> getSolutionByPublicId(@Valid @PathVariable String publicId) {
        try {
            SolutionResponse solutionResponse = solutionService.getSolutionByPublicId(publicId);
            return ResponseEntity.status(HttpStatus.OK).body(solutionResponse);
        } catch (BadRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    /**
     * Get All Submitted Solutions by current user
     *
     * @return The all Submitted Solutions by current user
     */
    @GetMapping
    public ResponseEntity<?> getMySubmittedSolutions() throws BadRequestException {
        try {
            List<SolutionResponse> solutions = solutionService.getMySubmittedSolutions();
            return ResponseEntity.status(HttpStatus.OK).body(solutions);
        } catch (BadRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    /**
     * Get Solutions for the quiz
     *
     * @param publicId The public ID of the quiz
     * @return Solutions for the quiz
     */
    @GetMapping("/quiz/{publicId}")
    public ResponseEntity<?> getSolutionsForQuiz(@Valid @PathVariable String publicId) throws BadRequestException {
        try {
            List<SolutionResponse> solutions = solutionService.getSolutionsForQuiz(publicId);
            return ResponseEntity.status(HttpStatus.OK).body(solutions);
        } catch (BadRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    /**
     * Get Solutions for the quiz
     *
     * @param publicId The public ID of the quiz
     * @param solutionRequest  The solution data
     * @return Solutions for the quiz
     */
    @PostMapping("{publicId}")
    public ResponseEntity<?> submitSolution(@Valid @PathVariable String publicId, @RequestBody SolutionRequest
            solutionRequest) throws BadRequestException {
        try {
            SolutionResponse solutionResponse = solutionService.submitSolution(publicId, solutionRequest);
            return ResponseEntity.status(HttpStatus.OK).body(solutionResponse);
        } catch (BadRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
