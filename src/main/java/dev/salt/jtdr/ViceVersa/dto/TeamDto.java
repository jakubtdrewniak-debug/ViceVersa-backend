package dev.salt.jtdr.ViceVersa.dto;

import java.util.List;

public record TeamDto(
        String id,
        String name,
        String avatar,
        UserDto captain,
        List<UserDto> members
) {
}
