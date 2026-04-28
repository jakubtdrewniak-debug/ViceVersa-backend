package dev.salt.jtdr.ViceVersa.service;

import dev.salt.jtdr.ViceVersa.domain.MatchEntity;
import dev.salt.jtdr.ViceVersa.domain.TournamentEntity;
import dev.salt.jtdr.ViceVersa.enums.MatchStatus;
import dev.salt.jtdr.ViceVersa.repository.match.MatchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BracketEngineService {

    private final MatchRepository repo;

    @Transactional
    public void generateBracket(TournamentEntity tournament, List<String> participantIds) {
        List<String> shuffledIds = new ArrayList<>(participantIds);
        Collections.shuffle(shuffledIds);

        int p = participantIds.size();
        int n = calculateNextPowerOfTwo(p);
        int totalMatches = n - 1;

        Queue<MatchEntity> queue = new LinkedList<>();

        MatchEntity finalMatch = createEmptyMatch(tournament, 1, null, false);
        queue.add(finalMatch);

        List<MatchEntity> allMatches = new ArrayList<>();
        allMatches.add(finalMatch);

        while (allMatches.size() < totalMatches) {
            MatchEntity parent = queue.poll();
            for (int i = 0; i < 2; i++) {
                MatchEntity child = createEmptyMatch(tournament, parent.getRound() + 1, parent.getId(), i == 0);
                queue.add(child);
                allMatches.add(child);
            }
        }

        List<MatchEntity> leafMatches = allMatches.stream()
                .filter(m -> allMatches.stream().noneMatch(c -> m.getId().equals(c.getNextMatchId())))
                .sorted(Comparator.comparing(MatchEntity::getId))
                .toList();

        int participantIndex = 0;
        int byes = n - p;

        for (MatchEntity leaf : leafMatches) {
            leaf.setPlayer1Id(shuffledIds.get(participantIndex++));
            if (byes > 0) {
                leaf.setWinnerId(leaf.getPlayer1Id());
                leaf.setStatus(MatchStatus.COMPLETED);
                advanceWinner(leaf.getWinnerId(), leaf.getNextMatchId(), leaf.isPlayer1Slot());
                byes--;
            } else {
                leaf.setPlayer2Id(shuffledIds.get(participantIndex++));
            }
            repo.saveMatch(leaf);
        }
    }

    public void advanceWinner(String winnerId, String nextMatchId, boolean isPlayer1Slot) {
        if (nextMatchId == null) return;

        MatchEntity nextMatch = repo.findMatch(nextMatchId)
                .orElseThrow(() -> new RuntimeException("Next match not found"));

        if (isPlayer1Slot) {
            nextMatch.setPlayer1Id(winnerId);
        } else {
            nextMatch.setPlayer2Id(winnerId);
        }
        repo.saveMatch(nextMatch);
    }

    private MatchEntity createEmptyMatch(TournamentEntity tournament, int round, String nextMatchId, boolean isPlayer1Slot) {
        MatchEntity match = new MatchEntity();


        match.setTournament(tournament);
        match.setEntryType(tournament.getEntryType());


        match.setRound(round);
        match.setNextMatchId(nextMatchId);
        match.setPlayer1Slot(isPlayer1Slot);

        match.setStatus(MatchStatus.PENDING);
        match.setMatchDate(LocalDateTime.now());

        return repo.saveMatch(match);
    }

    private int calculateNextPowerOfTwo(int p) {
        int n = 1;
        while (n < p) {
            n *= 2;
        }
        return n;
    }
}