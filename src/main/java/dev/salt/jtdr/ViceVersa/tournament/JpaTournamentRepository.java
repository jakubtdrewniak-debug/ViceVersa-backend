package dev.salt.jtdr.ViceVersa.tournament;

import dev.salt.jtdr.ViceVersa.domain.TournamentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTournamentRepository extends JpaRepository<TournamentEntity, String> {
}
