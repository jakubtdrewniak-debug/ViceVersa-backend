package dev.salt.jtdr.ViceVersa.service.user;

import dev.salt.jtdr.ViceVersa.domain.UserEntity;
import dev.salt.jtdr.ViceVersa.dto.UserDto;
import dev.salt.jtdr.ViceVersa.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;

    public List<UserDto> getAllUsers() {
        return repo.findAll().stream()
                .map(this::mapToUserDto)
                .toList();
    }

    private UserDto mapToUserDto(UserEntity user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getAvatar(),
                user.getEmail()
        );
    }
}
