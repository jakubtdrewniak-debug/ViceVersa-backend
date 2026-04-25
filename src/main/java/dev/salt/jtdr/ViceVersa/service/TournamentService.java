package dev.salt.jtdr.ViceVersa.service;

import dev.salt.jtdr.ViceVersa.domain.MatchEntity;
import dev.salt.jtdr.ViceVersa.domain.TournamentEntity;
import dev.salt.jtdr.ViceVersa.dto.*;
import dev.salt.jtdr.ViceVersa.enums.EntryType;
import dev.salt.jtdr.ViceVersa.team.TeamRepository;
import dev.salt.jtdr.ViceVersa.tournament.TournamentRepository;
import dev.salt.jtdr.ViceVersa.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepo;
    private final UserRepository userRepo;
    private final TeamRepository teamRepo;

    public TournamentDto getTournamentDetails(String tournamentId) {

        Optional<TournamentEntity> tournamentOptional = tournamentRepo.findById(tournamentId);

        if (tournamentOptional.isEmpty()) {
            return null;
        }
        TournamentEntity tournament = tournamentOptional.get();

        List<MatchDto> matches = tournament.getMatches().stream()
                .map(this::mapToMatchDto)
                .toList();

        return new TournamentDto(
                tournament.getId(),
                tournament.getName(),
                tournament.getGame(),
                tournament.getEntryType(),
                tournament.getStatus(),
                matches
        );
    }

    @Transactional
    public TournamentDto updateTournament(String tournamentId, TournamentUpdateDto dto) {
        TournamentEntity tournament = tournamentRepo.findById(tournamentId)
                .orElse(null);

        if (tournament == null) return null;

        if (dto.name() != null) tournament.setName(dto.name());
        if (dto.game() != null) tournament.setGame(dto.game());
        if (dto.status() != null) tournament.setStatus(dto.status());

        tournamentRepo.updateTournament(tournament);

        return getTournamentDetails(tournamentId);
    }

    @Transactional
    public boolean deleteTournament(String tournamentId) {
        if (!tournamentRepo.exists(tournamentId)) {
            return false;
        }
        tournamentRepo.deleteById(tournamentId);
        return true;
    }

    private MatchDto mapToMatchDto(MatchEntity match) {
        ParticipantDto p1 = resolveParticipant(match.getPlayer1Id(), match.getEntryType());
        ParticipantDto p2 = resolveParticipant(match.getPlayer2Id(), match.getEntryType());
        ParticipantDto winner = resolveParticipant(match.getWinnerId(), match.getEntryType());

        return new MatchDto(
                match.getId(),
                match.getTournament() != null ? match.getTournament().getId() : null,
                match.getRound(),
                match.getMatchDate(),
                match.getStatus(),
                p1,
                p2,
                winner,
                new MatchScoreDto(match.getScoreP1(), match.getScoreP2())
        );
    }

    private ParticipantDto resolveParticipant(String id, EntryType type) {
        if (id == null) return null;

        if (type == EntryType.SOLO) {
            return userRepo.findById(id)
                    .map(user -> new ParticipantDto(user.getId(), user.getName(), user.getAvatar(), false))
                    .orElse(new ParticipantDto(id, "Unknown Player", null, false));
        } else {
            return teamRepo.findById(id)
                    .map(team -> new ParticipantDto(team.getId(), team.getName(), team.getAvatar(), true))
                    .orElse(new ParticipantDto(id, "Unknown Team", null, true));
        }
    }
}
