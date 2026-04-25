package dev.salt.jtdr.ViceVersa.tournament;

import dev.salt.jtdr.ViceVersa.domain.TournamentEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TournamentRepository {

    private final JpaTournamentRepository repo;

    public TournamentRepository(JpaTournamentRepository repo) {
        this.repo = repo;
    }

    public Optional<TournamentEntity> findById(String tournamentId) {
        return repo.findById(tournamentId);
    }

    public void updateTournament(TournamentEntity tournament) {
        repo.save(tournament);
    }

    public boolean exists(String tournamentId) {
        return repo.existsById(tournamentId);
    }

    public void deleteById(String tournamentId) {
        repo.deleteById(tournamentId);
    }

    public TournamentEntity saveTournament(TournamentEntity newTournament) {
        return repo.save(newTournament);
    }
}
