package dev.salt.jtdr.ViceVersa.service.match;

import dev.salt.jtdr.ViceVersa.domain.MatchEntity;
import dev.salt.jtdr.ViceVersa.dto.match.MatchDto;
import dev.salt.jtdr.ViceVersa.enums.EntryType;
import dev.salt.jtdr.ViceVersa.enums.MatchStatus;
import dev.salt.jtdr.ViceVersa.repository.match.MatchRepository;
import dev.salt.jtdr.ViceVersa.service.TournamentProgressionService;
import dev.salt.jtdr.ViceVersa.service.helper.MatchMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepo;
    private final TournamentProgressionService progressionService;
    private final MatchMapper mapper;

    public MatchDto getMatchById(String matchId) {
        return matchRepo.findMatch(matchId)
                .map(mapper::mapToMatchDto)
                .orElse(null);
    }

    public List<MatchDto> getAllMatches() {
        return matchRepo.findAllMatches().stream()
                .map(mapper::mapToMatchDto)
                .toList();
    }

    public List<MatchDto> getSoloMatches() {
        return matchRepo.findSoloMatches().stream()
                .map(mapper::mapToMatchDto)
                .toList();
    }

    public List<MatchDto> getPlayerHistory(String playerId) {
        return matchRepo.findHistory(playerId).stream()
                .map(mapper::mapToMatchDto)
                .toList();
    }

    @Transactional
    public MatchDto createExhibitionMatch(String requestId, EntryType type, String player1Id, String player2Id) {
        if (!requestId.equals(player1Id) && !requestId.equals(player2Id)) {
            throw new SecurityException("You can create an exhibition match only if you participate in it.");
        }
        MatchEntity match = new MatchEntity();
        match.setEntryType(type);
        match.setPlayer1Id(player1Id);
        match.setPlayer2Id(player2Id);
        match.setMatchDate(LocalDateTime.now());
        match.setStatus(MatchStatus.PENDING);

        MatchEntity savedMatch = matchRepo.saveMatch(match);
        return mapper.mapToMatchDto(savedMatch);
    }

    @Transactional
    public MatchDto updateMatchScore(String matchId, String requestId, Integer scoreP1, Integer scoreP2, boolean isAdmin) {
        MatchEntity match = matchRepo.findMatch(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found"));

        boolean isPlayer1 = requestId.equals(match.getPlayer1Id());
        boolean isPlayer2 = requestId.equals(match.getPlayer2Id());

        if (!isAdmin && !isPlayer1 && !isPlayer2) {
            throw new SecurityException("Only match participants and admins can report match scores.");
        }

        if (scoreP1 < 0 || scoreP2 < 0) {
            throw new IllegalArgumentException("Scores cannot be negative");
        }

        match.setScoreP1(scoreP1);
        match.setScoreP2(scoreP2);
        match.setStatus(MatchStatus.COMPLETED);

        if (scoreP1 > scoreP2) {
            match.setWinnerId(match.getPlayer1Id());
        } else if (scoreP2 > scoreP1) {
            match.setWinnerId(match.getPlayer2Id());
        } else {
            match.setWinnerId(null);
        }

        matchRepo.saveMatch(match);

        progressionService.advanceWinner(match);
        return mapper.mapToMatchDto(match);
    }


    @Transactional
    public boolean deleteMatch(String matchId, boolean isAdmin) {
        if (!isAdmin) {
            throw new SecurityException("Only administrators can delete match records.");
        }

        if (!matchRepo.exists(matchId)) {
            return false;
        }
        matchRepo.deleteById(matchId);
        return true;
    }
}
