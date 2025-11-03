package com.example.voting.controller;

import com.example.voting.model.Candidate;
import com.example.voting.model.Voter;
import com.example.voting.repo.CandidateRepository;
import com.example.voting.repo.VoterRepository;
import com.example.voting.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final VoterRepository voterRepo;
    private final CandidateRepository candidateRepo;
    private final AuthService authService;

    public ApiController(VoterRepository voterRepo, CandidateRepository candidateRepo, AuthService authService) {
        this.voterRepo = voterRepo;
        this.candidateRepo = candidateRepo;
        this.authService = authService;
    }

    // ------------------- AUTH -------------------

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestParam String username, @RequestParam String password) {
        try {
            Voter v = authService.register(username, password);
            return ResponseEntity.ok(Map.of("message", "Registration successful", "id", v.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            String token = authService.login(username, password);
            Voter v = authService.findByToken(token);
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "role", v.getRole(),
                    "message", "Login successful"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ------------------- USER / VOTING -------------------

    @GetMapping("/candidates")
    public ResponseEntity<?> getCandidates(@RequestHeader("X-AUTH-TOKEN") String token) {
        Voter v = authService.findByToken(token);
        if (v == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        return ResponseEntity.ok(candidateRepo.findAll());
    }

    @PostMapping("/vote")
    public ResponseEntity<?> vote(@RequestHeader("X-AUTH-TOKEN") String token, @RequestParam Long candidateId) {
        Voter v = authService.findByToken(token);
        if (v == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        if (v.isHasVoted()) {
            return ResponseEntity.badRequest().body(Map.of("error", "You have already voted"));
        }

        Candidate c = candidateRepo.findById(candidateId).orElse(null);
        if (c == null) return ResponseEntity.badRequest().body(Map.of("error", "Candidate not found"));

        c.setVotesCount(c.getVotesCount() + 1);
        candidateRepo.save(c);
        v.setHasVoted(true);
        voterRepo.save(v);

        return ResponseEntity.ok(Map.of("message", "Vote recorded successfully"));
    }

    @GetMapping("/results")
    public ResponseEntity<?> results() {
        List<Candidate> list = candidateRepo.findAll();
        list.sort((a, b) -> b.getVotesCount() - a.getVotesCount());
        return ResponseEntity.ok(list);
    }

    // ------------------- ADMIN ONLY -------------------

    @PostMapping("/admin/candidate")
    public ResponseEntity<?> addCandidate(@RequestHeader("X-AUTH-TOKEN") String token,
                                          @RequestParam String name,
                                          @RequestParam(required = false) String party) {
        Voter v = authService.findByToken(token);
        if (v == null || !"ADMIN".equals(v.getRole())) {
            return ResponseEntity.status(403).body(Map.of("error", "Admin only"));
        }

        Candidate c = new Candidate();
        c.setName(name);
        c.setParty(party);
        c.setVotesCount(0);
        candidateRepo.save(c);

        return ResponseEntity.ok(Map.of("message", "Candidate added successfully"));
    }

    @DeleteMapping("/admin/candidate/{id}")
    public ResponseEntity<?> deleteCandidate(@RequestHeader("X-AUTH-TOKEN") String token,
                                             @PathVariable Long id) {
        Voter v = authService.findByToken(token);
        if (v == null || !"ADMIN".equals(v.getRole())) {
            return ResponseEntity.status(403).body(Map.of("error", "Admin only"));
        }

        if (!candidateRepo.existsById(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Candidate not found"));
        }

        candidateRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Candidate deleted successfully"));
    }

    @GetMapping("/admin/voters")
    public ResponseEntity<?> listVoters(@RequestHeader("X-AUTH-TOKEN") String token) {
        Voter v = authService.findByToken(token);
        if (v == null || !"ADMIN".equals(v.getRole())) {
            return ResponseEntity.status(403).body(Map.of("error", "Admin only"));
        }

        return ResponseEntity.ok(voterRepo.findAll());
    }
}
