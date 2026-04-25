package dev.salt.jtdr.ViceVersa.dto.match;

import dev.salt.jtdr.ViceVersa.dto.ParticipantDto;
import dev.salt.jtdr.ViceVersa.enums.MatchStatus;

import java.time.LocalDateTime;

public record MatchDto(
        String id,
        String tournamentId,
        Integer round,
        LocalDateTime date,
        MatchStatus status,
        ParticipantDto player1,
        ParticipantDto player2,
        ParticipantDto winner,
        MatchScoreDto score
) {
}
