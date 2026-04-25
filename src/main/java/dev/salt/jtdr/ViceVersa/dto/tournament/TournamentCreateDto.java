package dev.salt.jtdr.ViceVersa.dto.tournament;

import dev.salt.jtdr.ViceVersa.enums.EntryType;

public record TournamentCreateDto(
        String name,
        String game,
        EntryType type
) {
}
