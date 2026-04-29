package dev.salt.jtdr.ViceVersa.controller;

import dev.salt.jtdr.ViceVersa.dto.team.TeamDto;
import dev.salt.jtdr.ViceVersa.dto.team.TeamUpdateDto;
import dev.salt.jtdr.ViceVersa.service.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<List<TeamDto>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDto> getTeam(@PathVariable String id) {
        try {
            TeamDto team = teamService.getTeamDetails(id);
            return ResponseEntity.ok(team);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<TeamDto> createTeam(
            @RequestParam String name,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String captainId = jwt.getSubject();

        TeamDto newTeam = teamService.createTeam(name, captainId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTeam);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTeam(
            @PathVariable String id,
            @RequestBody TeamUpdateDto dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        try {
            String requestId = jwt.getSubject();
            TeamDto updatedTeam = teamService.updateTeamDetails(id, requestId, dto);
            return ResponseEntity.ok(updatedTeam);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/members/{userId}")
    public ResponseEntity<?> addMember(
            @PathVariable String id,
            @PathVariable String userId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        try {
            String requestId = jwt.getSubject();
            TeamDto updatedTeam = teamService.addMember(id, requestId, userId);
            return ResponseEntity.ok(updatedTeam);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<?> removeMember(
            @PathVariable String id,
            @PathVariable String userId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        try {
            String requestId = jwt.getSubject();
            TeamDto updatedTeam = teamService.removeMember(id, requestId, userId);
            return ResponseEntity.ok(updatedTeam);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        try {
            String requestId = jwt.getSubject();
            List<String> roles = jwt.getClaimAsStringList("https://viceversa.dev/roles");
            boolean isAdmin = roles != null && roles.contains("admin");

            boolean deleted = teamService.deleteTeam(id, requestId, isAdmin);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

}
