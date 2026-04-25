package dev.salt.jtdr.ViceVersa.repository.team;

import dev.salt.jtdr.ViceVersa.domain.TeamEntity;
import dev.salt.jtdr.ViceVersa.domain.UserEntity;
import dev.salt.jtdr.ViceVersa.dto.team.TeamDto;
import dev.salt.jtdr.ViceVersa.repository.user.JpaUserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    public TeamEntity createTeam(TeamEntity newTeam) {
        return repo.save(newTeam);
    }

    public void deleteTeam(String teamId) {
        repo.deleteById(teamId);
    }

    public void saveTeam(TeamEntity team) {
        repo.save(team);
    }

    public List<TeamEntity> findAllTeams() {
        return repo.findAll();
    }
}
