package dev.salt.jtdr.ViceVersa.dto;

import dev.salt.jtdr.ViceVersa.enums.TournamentStatus;

public record TournamentUpdateDto(
        String name,
        String game,
        TournamentStatus status
) {
}
