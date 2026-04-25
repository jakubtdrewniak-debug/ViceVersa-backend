package dev.salt.jtdr.ViceVersa.service.match;

import dev.salt.jtdr.ViceVersa.domain.MatchEntity;
import dev.salt.jtdr.ViceVersa.dto.match.MatchDto;
import dev.salt.jtdr.ViceVersa.enums.EntryType;
import dev.salt.jtdr.ViceVersa.enums.MatchStatus;
import dev.salt.jtdr.ViceVersa.repository.match.MatchRepository;
import dev.salt.jtdr.ViceVersa.service.helper.MatchMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepo;

    private final MatchMapper mapper;

    public MatchDto getMatchById(String matchId) {
        return matchRepo.findMatch(matchId)
                .map(mapper::mapToMatchDto)
                .orElse(null);
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
    public MatchDto createExhibitionMatch(EntryType type, String player1Id, String player2Id) {
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
    public MatchDto updateMatchScore(String matchId, Integer scoreP1, Integer scoreP2) {
        MatchEntity match = matchRepo.findMatch(matchId).orElse(null);
        if (match == null) return null;

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
        return mapper.mapToMatchDto(match);
    }

    @Transactional
    public boolean deleteMatch(String matchId) {
        if (!matchRepo.exists(matchId)) {
            return false;
        }
        matchRepo.deleteById(matchId);
        return true;
    }
}
