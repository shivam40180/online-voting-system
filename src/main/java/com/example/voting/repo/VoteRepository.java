package com.example.voting.repo;

import com.example.voting.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByVoterId(Long voterId);
}
