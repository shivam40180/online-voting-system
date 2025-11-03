package com.example.voting.service;

import com.example.voting.model.Candidate;
import com.example.voting.model.Voter;
import com.example.voting.repo.CandidateRepository;
import com.example.voting.repo.VoterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoteService {

    private final CandidateRepository candidateRepo;
    private final VoterRepository voterRepo;

    public VoteService(CandidateRepository candidateRepo, VoterRepository voterRepo) {
        this.candidateRepo = candidateRepo;
        this.voterRepo = voterRepo;
    }

    // ✅ Get all candidates
    public List<Candidate> getAllCandidates() {
        return candidateRepo.findAll();
    }

    // ✅ Record vote
    public void vote(Long voterId, Long candidateId) {
        Voter voter = voterRepo.findById(voterId).orElseThrow(() -> new RuntimeException("Voter not found"));
        Candidate candidate = candidateRepo.findById(candidateId).orElseThrow(() -> new RuntimeException("Candidate not found"));

        if (voter.isHasVoted()) {
            throw new RuntimeException("You have already voted!");
        }

        // ✅ Use getter/setter instead of direct access
        candidate.setVotesCount(candidate.getVotesCount() + 1);
        candidateRepo.save(candidate);

        voter.setHasVoted(true);
        voterRepo.save(voter);
    }
}
