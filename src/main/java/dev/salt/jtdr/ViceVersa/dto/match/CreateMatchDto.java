package dev.salt.jtdr.ViceVersa.dto.match;

import dev.salt.jtdr.ViceVersa.enums.EntryType;

public record CreateMatchDto(
        EntryType type,
        String player1Id,
        String player2Id
) {
}
