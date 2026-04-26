package dev.salt.jtdr.ViceVersa.service;

import dev.salt.jtdr.ViceVersa.domain.MatchEntity;
import dev.salt.jtdr.ViceVersa.domain.TournamentEntity;
import dev.salt.jtdr.ViceVersa.enums.MatchStatus;
import dev.salt.jtdr.ViceVersa.repository.match.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BracketEngineService {

    private final MatchRepository repo;

    public List<MatchEntity> generateBracket(TournamentEntity tournament, List<String> participantIds) {

        List<String> shuffledIds = new ArrayList<>(participantIds);
        Collections.shuffle(shuffledIds);

        int p = participantIds.size();
        int n = calculateNextPowerOfTwo(p);
        int byes = n - p;
        int totalRound1Matches = n / 2;

        List<MatchEntity> generatedMatches = new ArrayList<>();
        int participantIndex = 0;

        for (int i = 0; i < totalRound1Matches; i++) {
            MatchEntity match = new MatchEntity();
            match.setTournament(tournament);
            match.setEntryType(tournament.getEntryType());
            match.setRound(1);
            match.setMatchDate(LocalDateTime.now());

            match.setPlayer1Id(shuffledIds.get(participantIndex++));

            if (byes > 0) {
                match.setPlayer2Id(null);
                match.setWinnerId(match.getPlayer1Id());
                match.setStatus(MatchStatus.COMPLETED);
                byes --;
            } else {
                match.setPlayer2Id(shuffledIds.get(participantIndex++));
                match.setStatus(MatchStatus.PENDING);
            }
            generatedMatches.add(match);
        }

        int currentRoundMathces = totalRound1Matches;
        int roundNumber = 2;

        while (currentRoundMathces > 1) {
            currentRoundMathces /= 2;
            for (int i = 0; i < currentRoundMathces; i++) {
                MatchEntity futureMatch = new MatchEntity();
                futureMatch.setTournament(tournament);
                futureMatch.setEntryType(tournament.getEntryType());
                futureMatch.setRound(roundNumber);
                futureMatch.setStatus(MatchStatus.PENDING);
                futureMatch.setPlayer1Id(null);
                futureMatch.setPlayer2Id(null);

                generatedMatches.add(futureMatch);
            }
            roundNumber++;
        }
        return repo.saveAll(generatedMatches);
    }

    private int calculateNextPowerOfTwo(int p) {
        int n = 1;
        while (n < p) {
            n *= 2;
        }
        return n;
    }
}