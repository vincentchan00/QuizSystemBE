package org.example.intvincentchan00.repository;

import org.example.intvincentchan00.entity.QuestionSolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionSolutionRepository extends JpaRepository<QuestionSolution, Long> {
}
