package org.example.intvincentchan00.repository;

import org.example.intvincentchan00.entity.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {
    boolean existsByUserIdAndQuizId(Long userId, Long quizId);
    Optional<Solution> findByPublicId(String publicId);
    List<Solution> findByUserId(Long userId);
    List<Solution> findByQuizId(Long quizId);
}
