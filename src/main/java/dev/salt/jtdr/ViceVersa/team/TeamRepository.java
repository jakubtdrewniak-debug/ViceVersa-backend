package dev.salt.jtdr.ViceVersa.team;

import dev.salt.jtdr.ViceVersa.domain.TeamEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TeamRepository {

    private final JpaTeamRepository repo;

    public TeamRepository(JpaTeamRepository repo) {
        this.repo = repo;
    }

    public Optional<TeamEntity> findById(String id) {
        return repo.findById(id);
    }
}
