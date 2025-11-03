package com.example.voting.controller;

import com.example.voting.model.Candidate;
import com.example.voting.model.Voter;
import com.example.voting.service.AuthService;
import com.example.voting.service.VoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class PageController {

    private final AuthService authService;
    private final VoteService voteService;

    public PageController(AuthService authService, VoteService voteService) {
        this.authService = authService;
        this.voteService = voteService;
    }

    // Home page
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Registration page
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
                             @RequestParam String password,
                             Model model) {
        try {
            authService.register(username, password);
            model.addAttribute("message", "Registration successful. Please login.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    // Login page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Login logic — handles both admin and voter
    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          Model model,
                          HttpSession session) {
        try {
            Voter voter = authService.findByUsername(username);
            String token = authService.login(username, password);

            if (voter == null) {
                model.addAttribute("error", "Invalid username or password");
                return "login";
            }

            session.setAttribute("voter", voter);

            // Redirect admin to /admin (handled by AdminController)
            if ("admin".equalsIgnoreCase(voter.getUsername())) {
                return "redirect:/admin";
            }

            // Redirect normal user to voting page
            return "redirect:/vote";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    // Voting page
    @GetMapping("/vote")
    public String votePage(Model model, HttpSession session) {
        List<Candidate> list = voteService.getAllCandidates();
        model.addAttribute("candidates", list);

        Voter voter = (Voter) session.getAttribute("voter");
        model.addAttribute("voter", voter);

        return "vote";
    }

    // Submit vote
    @PostMapping("/vote")
    public String doVote(@RequestParam Long voterId,
                         @RequestParam Long candidateId,
                         Model model) {
        try {
            voteService.vote(voterId, candidateId);
            model.addAttribute("message", "Vote recorded successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("candidates", voteService.getAllCandidates());
        return "results";
    }

    // Results page
    @GetMapping("/results")
    public String resultsPage(Model model) {
        model.addAttribute("candidates", voteService.getAllCandidates());
        return "results";
    }
}
