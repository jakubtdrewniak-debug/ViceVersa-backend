package dev.salt.jtdr.ViceVersa.dto.tournament;

import dev.salt.jtdr.ViceVersa.dto.match.MatchDto;
import dev.salt.jtdr.ViceVersa.enums.EntryType;
import dev.salt.jtdr.ViceVersa.enums.TournamentStatus;

import java.util.List;

public record TournamentDto(
        String id,
        String name,
        String game,
        EntryType type,
        TournamentStatus status,
        List<MatchDto> matches
) {
}
