package org.example.intvincentchan00.repository;

import org.example.intvincentchan00.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Modifying
    @Query("DELETE FROM Answer a WHERE a.question.quiz.id = :quizId")
    void deleteByQuizId(@Param("quizId") Long quizId);
    List<Answer> findByQuestionId(Long questionId);
}

