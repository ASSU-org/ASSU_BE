package com.assu.server.domain.suggestion.repository;

import com.assu.server.domain.suggestion.entity.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

    @Query("""
           select s
           from Suggestion s
           join fetch s.student st
           where s.admin.id = :adminId
           order by s.createdAt desc
           """)
    List<Suggestion> findAllSuggestions(@Param("adminId") Long adminId);
}
