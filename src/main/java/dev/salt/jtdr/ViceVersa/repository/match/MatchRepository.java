package dev.salt.jtdr.ViceVersa.repository.match;

import dev.salt.jtdr.ViceVersa.domain.MatchEntity;
import dev.salt.jtdr.ViceVersa.dto.match.MatchDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MatchRepository {

    private final JpaMatchRepository repo;

    public MatchRepository(JpaMatchRepository repo) {
        this.repo = repo;
    }

    public Optional<MatchEntity> findMatch(String matchId) {
        return repo.findById(matchId);
    }

    public List<MatchEntity> findSoloMatches() {
        return repo.findByTournamentIsNull();
    }


    public MatchEntity saveMatch(MatchEntity match) {
        repo.save(match);
    }

    public List<MatchEntity> findHistory(String playerId) {
        return repo.findAllByPlayer1IdOrPlayer2Id(playerId, playerId);
    }

    public boolean exists(String matchId) {
        return repo.existsById(matchId);
    }

    public void deleteById(String matchId) {
        repo.deleteById(matchId);
    }
}
