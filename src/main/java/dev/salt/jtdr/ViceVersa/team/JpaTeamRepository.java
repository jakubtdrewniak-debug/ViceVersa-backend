package dev.salt.jtdr.ViceVersa.team;

import dev.salt.jtdr.ViceVersa.domain.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTeamRepository extends JpaRepository<TeamEntity, String> {
}
