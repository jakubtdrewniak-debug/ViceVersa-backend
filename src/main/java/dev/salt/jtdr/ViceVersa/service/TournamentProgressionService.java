package dev.salt.jtdr.ViceVersa.service;

import dev.salt.jtdr.ViceVersa.domain.MatchEntity;
import dev.salt.jtdr.ViceVersa.domain.TournamentEntity;
import dev.salt.jtdr.ViceVersa.enums.TournamentStatus;
import dev.salt.jtdr.ViceVersa.repository.match.MatchRepository;
import dev.salt.jtdr.ViceVersa.repository.tournament.TournamentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TournamentProgressionService {

    private final MatchRepository matchRepo;
    private final TournamentRepository tournamentRepo;

    @Transactional
    public void advanceWinner(MatchEntity completedMatch) {
        if (completedMatch.getTournament() == null) return;
        String winnerId = completedMatch.getWinnerId();
        if (winnerId == null) return;

        if (completedMatch.getNextMatchId() == null) {

            finalizeTournament(completedMatch.getTournament().getId(), winnerId);

        } else {


            MatchEntity nextMatch = matchRepo.findMatch(completedMatch.getNextMatchId())
                    .orElseThrow(() -> new NoSuchElementException("Next match not found"));


            if (completedMatch.isPlayer1Slot()) {
                nextMatch.setPlayer1Id(winnerId);
            } else {
                nextMatch.setPlayer2Id(winnerId);
            }

            matchRepo.save(nextMatch);
        }
    }

    private void finalizeTournament(String tournamentId, String winnerId) {
        TournamentEntity tournament = tournamentRepo.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        tournament.setWinnerId(winnerId);
        tournament.setStatus(TournamentStatus.COMPLETED);

        tournamentRepo.saveTournament(tournament);
    }

}
