package org.example.intvincentchan00.repository;

import org.example.intvincentchan00.entity.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity.
 * Provides methods to interact with the user data in the database.
 */
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findAllByPublishedIsTrue();

    Optional<Quiz> findByPublicId(String publicId);

    List<Quiz> findByAuthorId(Long authorId);

    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions qst LEFT JOIN FETCH qst.answers WHERE q.publicId = :publicId")
    Optional<Quiz> findByPublicIdWithDetails(@Param("publicId") String publicId);

}
