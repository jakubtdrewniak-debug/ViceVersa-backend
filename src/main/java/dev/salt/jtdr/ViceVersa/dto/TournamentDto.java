package dev.salt.jtdr.ViceVersa.dto;

import dev.salt.jtdr.ViceVersa.enums.EntryType;
import dev.salt.jtdr.ViceVersa.enums.TournamentStatus;

import java.util.List;
import java.util.Map;

public record TournamentDto(
        String id,
        String name,
        String game,
        EntryType type,
        TournamentStatus status,
        List<MatchDto> matches
) {
}
