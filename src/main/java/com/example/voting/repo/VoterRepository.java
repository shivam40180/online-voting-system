package com.example.voting.repo;

import com.example.voting.model.Voter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoterRepository extends JpaRepository<Voter, Long> {
    Voter findByUsername(String username);
}
