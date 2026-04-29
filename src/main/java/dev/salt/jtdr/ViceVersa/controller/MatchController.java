package dev.salt.jtdr.ViceVersa.controller;

import dev.salt.jtdr.ViceVersa.dto.match.CreateMatchDto;
import dev.salt.jtdr.ViceVersa.dto.match.MatchDto;
import dev.salt.jtdr.ViceVersa.dto.match.MatchScoreDto;
import dev.salt.jtdr.ViceVersa.service.match.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<List<MatchDto>> getAllMatches() {
        return ResponseEntity.ok(matchService.getAllMatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchDto> getMatch(@PathVariable String id) {
        try {
            MatchDto match = matchService.getMatchById(id);
            return ResponseEntity.ok(match);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exhibitions")
    public ResponseEntity<List<MatchDto>> getExhibitionMatches() {
        return ResponseEntity.ok(matchService.getSoloMatches());
    }

    @GetMapping("/history/{participantId}")
    public ResponseEntity<List<MatchDto>> getHistory(@PathVariable String participantId) {
        return ResponseEntity.ok(matchService.getPlayerHistory(participantId));
    }

    @PostMapping("/exhibitions")
    public ResponseEntity<?> createExhibition(
            @RequestBody CreateMatchDto dto,
            @AuthenticationPrincipal Jwt jwt
            ) {
        try {
            String requestId = jwt.getSubject();

            MatchDto newMatch = matchService.createExhibitionMatch(
                    requestId, dto.type(), dto.player1Id(), dto.player2Id()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(newMatch);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/score")
    public ResponseEntity<?> updateScore(
            @PathVariable String id,
            @RequestBody MatchScoreDto dto,
            Authentication authentication,
            @AuthenticationPrincipal Jwt jwt
            ) {
        try {
            String requestId = jwt.getSubject();

            boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("admin"));
            MatchDto updatedMatch = matchService.updateMatchScore(
                    id, requestId, dto.p1(), dto.p2(), isAdmin
            );
            return ResponseEntity.ok(updatedMatch);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteMatch(@PathVariable String id) {
        boolean deleted = matchService.deleteMatch(id, true);

        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
