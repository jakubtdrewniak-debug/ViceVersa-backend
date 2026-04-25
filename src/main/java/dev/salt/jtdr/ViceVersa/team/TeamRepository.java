package dev.salt.jtdr.ViceVersa.team;

import org.springframework.stereotype.Repository;

@Repository
public class TeamRepository {

    private final JpaTeamRepository repo;

    public TeamRepository(JpaTeamRepository repo) {
        this.repo = repo;
    }
}
