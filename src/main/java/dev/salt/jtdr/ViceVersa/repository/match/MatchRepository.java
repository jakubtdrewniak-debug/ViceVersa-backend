package dev.salt.jtdr.ViceVersa.repository.match;

import dev.salt.jtdr.ViceVersa.domain.MatchEntity;
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
        return repo.save(match);
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

    public List<MatchEntity> findAllMatches() {
        return repo.findAll();
    }

    public List<MatchEntity> saveAll(List<MatchEntity> generatedMatches) {
        return repo.saveAll(generatedMatches);
    }

    public void save(MatchEntity nextMatch) {
        repo.save(nextMatch);
    }

    public List<MatchEntity> findRoundMatches(String tournamentId, int currentRound) {
        return repo.findByTournamentIdAndRoundOrderByIdAsc(tournamentId, currentRound);
    }

    public List<MatchEntity> findMatchesInTournament(String tournamentId) {
        return repo.findByTournamentId(tournamentId);

    }
}
