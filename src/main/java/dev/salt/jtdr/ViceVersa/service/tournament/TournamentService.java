package dev.salt.jtdr.ViceVersa.service.tournament;

import dev.salt.jtdr.ViceVersa.domain.MatchEntity;
import dev.salt.jtdr.ViceVersa.domain.TeamEntity;
import dev.salt.jtdr.ViceVersa.domain.TournamentEntity;
import dev.salt.jtdr.ViceVersa.domain.UserEntity;
import dev.salt.jtdr.ViceVersa.dto.*;
import dev.salt.jtdr.ViceVersa.dto.match.MatchDto;
import dev.salt.jtdr.ViceVersa.dto.tournament.TournamentCreateDto;
import dev.salt.jtdr.ViceVersa.dto.tournament.TournamentDto;
import dev.salt.jtdr.ViceVersa.dto.tournament.TournamentUpdateDto;
import dev.salt.jtdr.ViceVersa.enums.EntryType;
import dev.salt.jtdr.ViceVersa.enums.TournamentStatus;
import dev.salt.jtdr.ViceVersa.repository.match.MatchRepository;
import dev.salt.jtdr.ViceVersa.repository.team.TeamRepository;
import dev.salt.jtdr.ViceVersa.repository.tournament.TournamentRepository;
import dev.salt.jtdr.ViceVersa.repository.user.UserRepository;
import dev.salt.jtdr.ViceVersa.service.BracketEngineService;
import dev.salt.jtdr.ViceVersa.service.helper.MatchMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepo;
    private final BracketEngineService bracket;
    private final MatchRepository matchRepo;
    private final MatchMapper mapper;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public TournamentDto getTournamentDetails(String tournamentId) {

        Optional<TournamentEntity> tournamentOptional = tournamentRepo.findById(tournamentId);

        if (tournamentOptional.isEmpty()) {
            return null;
        }
        TournamentEntity tournament = tournamentOptional.get();

        List<MatchDto> matches = tournament.getMatches().stream()
                .map(mapper::mapToMatchDto)
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

    public List<TournamentDto> getAllTournaments() {
        return tournamentRepo.findAll().stream()
                .map(this::mapToTournamentDTO)
                .toList();
    }

    public List<ParticipantDto> getTournamentParticipants(String tournamentId) {
        TournamentEntity tournament = tournamentRepo.findById(tournamentId)
                .orElse(null);

        List<MatchEntity> matches = matchRepo.findMatchesInTournament(tournamentId);

        Set<String> uniqueIds = new HashSet<>();
        for (MatchEntity match : matches) {
            if (match.getPlayer1Id() != null) uniqueIds.add(match.getPlayer1Id());
            if (match.getPlayer2Id() != null) uniqueIds.add(match.getPlayer2Id());
        }

        if (EntryType.SOLO == tournament.getEntryType()) {

            List<UserEntity> users = userRepository.findAllId(uniqueIds);
            return users.stream()
                    .map(user -> new ParticipantDto(
                            user.getId(),
                            user.getName(),
                            user.getAvatar(),
                            false
                    )).collect(Collectors.toList());
        } else {
            List<TeamEntity> teams = teamRepository.findAllId(uniqueIds);
            return teams.stream()
                    .map(team -> new ParticipantDto(
                            team.getId(),
                            team.getName(),
                            team.getAvatar(),
                            true
                    ))
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public TournamentDto createTournament(TournamentCreateDto createData) {
        TournamentEntity newTournament = new TournamentEntity();

        newTournament.setName(createData.name());
        newTournament.setGame(createData.game());
        newTournament.setEntryType(createData.type());;
        newTournament.setStatus(TournamentStatus.UPCOMING);

        TournamentEntity savedTournament = tournamentRepo.saveTournament(newTournament);

        return getTournamentDetails(savedTournament.getId());
    }


    @Transactional
    public TournamentDto startTournament(String tournamentId, List<String> participantIds) {
        TournamentEntity tournament = tournamentRepo.findById(tournamentId).orElse(null);
        if (tournament == null) return null;

        if (tournament.getStatus() != TournamentStatus.LIVE) {
            throw new IllegalStateException("Tournament has already been started.");
        }

        bracket.generateBracket(tournament, participantIds);

        tournamentRepo.saveTournament(tournament);

        return getTournamentDetails(tournamentId);
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

    private TournamentDto mapToTournamentDTO(TournamentEntity tournament) {
        List<MatchDto> matchDTOs = tournament.getMatches().stream()
                .map(mapper::mapToMatchDto)
                .toList();

        return new TournamentDto(
                tournament.getId(),
                tournament.getName(),
                tournament.getGame(),
                tournament.getEntryType(),
                tournament.getStatus(),
                matchDTOs
        );
    }

}
