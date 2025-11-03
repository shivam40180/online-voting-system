package com.example.voting.controller;

import com.example.voting.model.Candidate;
import com.example.voting.repo.CandidateRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CandidateRepository candidateRepo;

    public AdminController(CandidateRepository candidateRepo) {
        this.candidateRepo = candidateRepo;
    }

    // Admin dashboard page
    @GetMapping
    public String adminHome() {
        return "admin";
    }

    // Show add candidate form
    @GetMapping("/add")
    public String addCandidatePage() {
        return "addCandidate";
    }

    // Handle candidate form submission
    @PostMapping("/add")
    public String addCandidate(@RequestParam String name,
                               @RequestParam String party,
                               Model model) {
        Candidate c = new Candidate();
        c.setName(name);
        c.setParty(party);
        c.setVotesCount(0);
        candidateRepo.save(c);

        model.addAttribute("message", "✅ Candidate added successfully!");
        return "admin";
    }
}
