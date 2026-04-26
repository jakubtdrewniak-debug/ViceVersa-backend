package dev.salt.jtdr.ViceVersa.service;

import dev.salt.jtdr.ViceVersa.domain.MatchEntity;
import dev.salt.jtdr.ViceVersa.repository.match.MatchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentProgressionService {

    private final MatchRepository matchRepo;

    @Transactional
    public void advanceWinner(MatchEntity completedMatch) {
        if (completedMatch.getTournament() == null) return;
        String winnerId = completedMatch.getWinnerId();
        if (winnerId == null) return;

        String tournamentId = completedMatch.getTournament().getId();
        int currentRound = completedMatch.getRound();

        List<MatchEntity> thisRoundMatches = matchRepo.findRoundMatches(tournamentId, currentRound);
        List<MatchEntity> nextRoundMatches = matchRepo.findRoundMatches(tournamentId, currentRound + 1);

        if (nextRoundMatches.isEmpty()) {
            // The tournament is over! You could update the TournamentStatus to COMPLETED here.
            return;
        }

        int matchIndex = thisRoundMatches.indexOf(completedMatch);
        if (matchIndex == -1) return;

        int nextMatchIndex = matchIndex / 2;
        boolean isPlayer1Slot = (matchIndex % 2 == 0);

        MatchEntity nextMatch = nextRoundMatches.get(nextMatchIndex);

        if (isPlayer1Slot) {
            nextMatch.setPlayer1Id(winnerId);
        } else {
            nextMatch.setPlayer2Id(winnerId);
        }

        matchRepo.save(nextMatch);
    }
}
