package dev.salt.jtdr.ViceVersa.dto.tournament;

import dev.salt.jtdr.ViceVersa.enums.TournamentStatus;

public record TournamentUpdateDto(
        String name,
        String game,
        TournamentStatus status
) {
}
