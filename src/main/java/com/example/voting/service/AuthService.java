package com.example.voting.service;

import com.example.voting.model.Voter;
import com.example.voting.repo.VoterRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final VoterRepository voterRepo;
    // simple in-memory token map (token -> voterId)
    private final Map<String, Long> tokens = new HashMap<>();

    public AuthService(VoterRepository voterRepo) {
        this.voterRepo = voterRepo;
        // ensure admin exists (optional)
        if (voterRepo.findByUsername("admin") == null) {
            Voter admin = new Voter();
            admin.username = "admin";
            admin.password = "admin123";
            admin.role = "ADMIN";
            admin.hasVoted = false;
            voterRepo.save(admin);
        }
    }

    public Voter register(String username, String password) {
        if (voterRepo.findByUsername(username) != null) {
            throw new RuntimeException("Username already exists");
        }
        Voter v = new Voter();
        v.username = username;
        v.password = password;
        v.role = "USER";
        v.hasVoted = false;
        return voterRepo.save(v);
    }

    /**
     * Return a token string on successful login.
     */
    public String login(String username, String password) {
        Voter v = voterRepo.findByUsername(username);
        if (v == null) throw new RuntimeException("Invalid username or password");
        if (!v.password.equals(password)) throw new RuntimeException("Invalid username or password");
        String token = UUID.randomUUID().toString();
        tokens.put(token, v.id);
        return token;
    }

    public Long resolve(String token) {
        return tokens.get(token);
    }

    public Voter findByUsername(String username) {
        return voterRepo.findByUsername(username);
    }

    public Voter findByToken(String token) {
        Long id = tokens.get(token);
        if (id == null) return null;
        return voterRepo.findById(id).orElse(null);
    }
}
