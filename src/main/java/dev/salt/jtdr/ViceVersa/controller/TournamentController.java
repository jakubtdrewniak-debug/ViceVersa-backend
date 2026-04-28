package dev.salt.jtdr.ViceVersa.controller;

import dev.salt.jtdr.ViceVersa.dto.ParticipantDto;
import dev.salt.jtdr.ViceVersa.dto.tournament.TournamentCreateDto;
import dev.salt.jtdr.ViceVersa.dto.tournament.TournamentDto;
import dev.salt.jtdr.ViceVersa.dto.tournament.TournamentUpdateDto;
import dev.salt.jtdr.ViceVersa.service.tournament.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @GetMapping
    public ResponseEntity<List<TournamentDto>> getAllTournaments() {
        return ResponseEntity.ok(tournamentService.getAllTournaments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentDto> getTournament(@PathVariable String id) {
        TournamentDto tournament = tournamentService.getTournamentDetails(id);
        return tournament != null ? ResponseEntity.ok(tournament) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TournamentDto> createTournament(@RequestBody TournamentCreateDto dto) {
        if (dto.name() == null || dto.game() == null) {
            return ResponseEntity.badRequest().build();
        }

        TournamentDto newTournament = tournamentService.createTournament(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTournament);
    }

    @GetMapping("/{tournamentId}/participants")
    public ResponseEntity<List<ParticipantDto>> getTournamentParticipants(@PathVariable String tournamentId) {
        List<ParticipantDto> participants = tournamentService.getTournamentParticipants(tournamentId);

        return ResponseEntity.ok(participants);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TournamentDto> updateTournament(
            @PathVariable String id,
            @RequestBody TournamentUpdateDto dto
    ) {

        TournamentDto updatedTournament = tournamentService.updateTournament(id, dto);
        return updatedTournament != null ? ResponseEntity.ok(updatedTournament) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<?> startTournament(
            @PathVariable String id,
            @RequestBody List<String> participantIds) {
        try {
            TournamentDto startedTournament = tournamentService.startTournament(id, participantIds);
            return startedTournament != null ? ResponseEntity.ok(startedTournament) : ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteTournament(@PathVariable String id) {
        boolean deleted = tournamentService.deleteTournament(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
