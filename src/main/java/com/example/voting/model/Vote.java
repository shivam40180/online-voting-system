package com.example.voting.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "votes", uniqueConstraints = @UniqueConstraint(columnNames = "voter_id"))
public class Vote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "voter_id", nullable=false, unique=true)
    public Long voterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable=false)
    public Candidate candidate;

    public Instant timestamp = Instant.now();
}
