package dev.salt.jtdr.ViceVersa.repository.match;

import dev.salt.jtdr.ViceVersa.domain.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaMatchRepository extends JpaRepository<MatchEntity, String> {
    List<MatchEntity> findByTournamentIsNull();

    List<MatchEntity> findAllByPlayer1IdOrPlayer2Id(String playerId, String player2d);

    List<MatchEntity> findByTournamentIdAndRoundOrderByIdAsc(String tournamentId, int currentRound);

    List<MatchEntity> findByTournamentId(String tournamentId);
}
