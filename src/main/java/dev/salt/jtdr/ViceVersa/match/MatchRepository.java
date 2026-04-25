package dev.salt.jtdr.ViceVersa.match;

import org.springframework.stereotype.Repository;

@Repository
public class MatchRepository {

    private final JpaMatchRepository repo;

    public MatchRepository(JpaMatchRepository repo) {
        this.repo = repo;
    }
}
