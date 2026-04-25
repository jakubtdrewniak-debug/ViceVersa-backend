package dev.salt.jtdr.ViceVersa.service.helper;

import dev.salt.jtdr.ViceVersa.domain.MatchEntity;
import dev.salt.jtdr.ViceVersa.dto.ParticipantDto;
import dev.salt.jtdr.ViceVersa.dto.match.MatchDto;
import dev.salt.jtdr.ViceVersa.dto.match.MatchScoreDto;
import dev.salt.jtdr.ViceVersa.enums.EntryType;
import dev.salt.jtdr.ViceVersa.repository.team.TeamRepository;
import dev.salt.jtdr.ViceVersa.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchMapper {

    private final UserRepository userRepo;
    private final TeamRepository teamRepo;



    public MatchDto mapToMatchDto(MatchEntity match) {
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
