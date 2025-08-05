package com.assu.server.domain.suggestion.repository;

import com.assu.server.domain.suggestion.entity.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

}
