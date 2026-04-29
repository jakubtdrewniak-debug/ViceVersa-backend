package dev.salt.jtdr.ViceVersa.service.user;

import dev.salt.jtdr.ViceVersa.domain.UserEntity;
import dev.salt.jtdr.ViceVersa.dto.UserDto;
import dev.salt.jtdr.ViceVersa.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;


    public UserDto findUser(String id) {
        UserEntity foundUser = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No user found"));

        return mapToUserDto(foundUser);

    }

    public List<UserDto> getAllUsers() {
        return repo.findAll().stream()
                .map(this::mapToUserDto)
                .toList();
    }

    public List<UserDto> searchUsers(String query) {
        return repo.findAll().stream()
                .filter(user -> user.getName().toLowerCase().contains(query.toLowerCase()))
                .map(this::mapToUserDto)
                .toList();
    }

    @Transactional
    public UserDto syncUser(Jwt jwt) {
        String id = jwt.getSubject();

        String auth0Id = jwt.getSubject();

        UserEntity existingUser = repo.findById(auth0Id).orElseGet(() -> {
            UserEntity newUser = new UserEntity();
            newUser.setId(id);
            return newUser;
        });

        String email = jwt.getClaimAsString("https://viceversa.dev/email");
        String name = jwt.getClaimAsString("https://viceversa.dev/name");
        String picture = jwt.getClaimAsString("https://viceversa.dev/picture");

        existingUser.setEmail(email);

        if (name != null && !name.isEmpty()) {
            existingUser.setName(name);
        } else if (existingUser.getName() == null || existingUser.getName().equals("New Player")) {
            String fallback = (email != null && email.contains("@")) ? email.split("@")[0] : "NewPlayer";
            existingUser.setName(fallback);
        }

        existingUser.setAvatar(picture);

        UserEntity savedUser = repo.saveUser(existingUser);
        return mapToUserDto(savedUser);
    }

    public UserDto updateUser(String id, String nameToChange) {
        Optional<UserEntity> userToChange = repo.findById(id);

        if (userToChange.isPresent()) {
            UserEntity user = userToChange.get();
            user.setName(nameToChange);
            repo.saveUser(user);
            return mapToUserDto(user);
        } else {
            throw new NoSuchElementException("No user found");
        }
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
