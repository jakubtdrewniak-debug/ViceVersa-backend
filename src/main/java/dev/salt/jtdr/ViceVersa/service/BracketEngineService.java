package dev.salt.jtdr.ViceVersa.service;

import dev.salt.jtdr.ViceVersa.domain.TournamentEntity;
import dev.salt.jtdr.ViceVersa.repository.match.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BracketEngineService {

    private final MatchRepository repo;

    public void generateBracket(TournamentEntity tournament, List<String> participantIds) {

        int p = participantIds.size();
        int n = calculateNextPowerOfTwo(p);
        int byes = n - p;

        // TODO Loop through ids and decide which matches will be skipped
    }

    private int calculateNextPowerOfTwo(int p) {
        int n = 1;
        while (n < p) {
            n *= 2;
        }
        return n;
    }
}