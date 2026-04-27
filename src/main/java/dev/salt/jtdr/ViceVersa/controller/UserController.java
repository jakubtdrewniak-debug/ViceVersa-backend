package dev.salt.jtdr.ViceVersa.controller;

import dev.salt.jtdr.ViceVersa.domain.UserEntity;
import dev.salt.jtdr.ViceVersa.dto.UserDto;
import dev.salt.jtdr.ViceVersa.service.user.UserService;
import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(userService.searchUsers(search));
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/sync")
    public ResponseEntity<UserDto> syncAuth0User(@AuthenticationPrincipal Jwt jwt) {
        UserDto syncedUser = userService.syncUser(jwt);
        return ResponseEntity.ok(syncedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable String id,
            @RequestParam String name,
            @AuthenticationPrincipal Jwt jwt, ServletRequest servletRequest) {

        String requestId = jwt.getSubject();

        List<String> roles = jwt.getClaimAsStringList("roles");
        boolean isAdmin = roles != null && roles.contains("admin");

        if (!requestId.equals(id) && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to edit this profile");
        }

        try {
            UserDto updatedUser = userService.updateUser(id, name);
            return ResponseEntity.ok(updatedUser);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
