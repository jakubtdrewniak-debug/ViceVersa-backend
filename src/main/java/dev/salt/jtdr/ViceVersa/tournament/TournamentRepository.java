package dev.salt.jtdr.ViceVersa.tournament;

import org.springframework.stereotype.Repository;

@Repository
public class TournamentRepository {

    private final JpaTournamentRepository repo;

    public TournamentRepository(JpaTournamentRepository repo) {
        this.repo = repo;
    }
}
