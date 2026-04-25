package dev.salt.jtdr.ViceVersa.service.team;

import dev.salt.jtdr.ViceVersa.domain.TeamEntity;
import dev.salt.jtdr.ViceVersa.domain.UserEntity;
import dev.salt.jtdr.ViceVersa.dto.team.TeamDto;
import dev.salt.jtdr.ViceVersa.dto.team.TeamUpdateDto;
import dev.salt.jtdr.ViceVersa.dto.UserDto;
import dev.salt.jtdr.ViceVersa.repository.team.TeamRepository;
import dev.salt.jtdr.ViceVersa.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepo;
    private final UserRepository userRepo;

    public TeamDto getTeamDetails(String teamId) {
        return teamRepo.findById(teamId)
                .map(this::mapToTeamDto)
                .orElse(null);
    }

    public List<TeamDto> getAllTeams() {
        return teamRepo.findAllTeams().stream()
                .map(this::mapToTeamDto)
                .toList();
    }

    @Transactional
    public TeamDto createTeam(String teamName, String captainId) {
        UserEntity captain = userRepo.findById(captainId)
                .orElseThrow(() -> new IllegalArgumentException("User not found. Cannot create team."));

        TeamEntity newTeam = new TeamEntity();
        newTeam.setName(teamName);
        newTeam.setCaptain(captain);

        newTeam.getMembers().add(captain);

        TeamEntity savedTeam = teamRepo.createTeam(newTeam);

        return mapToTeamDto(savedTeam);
    }

    @Transactional
    public boolean deleteTeam(String teamId, String requesterId) {
        TeamEntity team = teamRepo.findById(teamId).orElse(null);

        if (team == null) return false;

        if (!team.getCaptain().getId().equals(requesterId)) {
            throw new SecurityException("Only the captain can delete this team.");
        }

        teamRepo.deleteTeam(teamId);
        return true;
    }

    @Transactional
    public TeamDto updateTeamDetails(String teamId, String requestId, TeamUpdateDto dto) {
        TeamEntity team = teamRepo.findById(teamId).orElse(null);
        if (team == null) return null;

        if (!team.getCaptain().getId().equals(requestId)) {
            throw new SecurityException("Only the captain can update team details");
        }

        if (dto.name() != null) team.setName(dto.name());
        if (dto.avatar() != null) team.setName(dto.avatar());

        teamRepo.saveTeam(team);
        return mapToTeamDto(team);
    }

    @Transactional
    public TeamDto addMember(String teamId, String requesterId, String newMemberId) {
        TeamEntity team = teamRepo.findById(teamId).orElse(null);
        if (team == null) return null;

        if (!team.getCaptain().getId().equals(requesterId)) {
            throw new SecurityException("Only the captain can add members.");
        }

        UserEntity newMember = userRepo.findById(newMemberId)
                .orElseThrow(() -> new IllegalArgumentException("User to add was not found."));

        boolean alreadyInTeam = team.getMembers().stream()
                .anyMatch(member -> member.getId().equals(newMemberId));

        if (!alreadyInTeam) {
            team.getMembers().add(newMember);
            teamRepo.saveTeam(team);
        }

        return mapToTeamDto(team);
    }

    @Transactional
    public TeamDto removeMember(String teamId, String requesterId, String memberToRemoveId) {
        TeamEntity team = teamRepo.findById(teamId).orElse(null);
        if (team == null) return null;

        if (!team.getCaptain().getId().equals(requesterId)) {
            throw new SecurityException("Only the captain can remove members.");
        }

        if (team.getCaptain().getId().equals(memberToRemoveId)) {
            throw new IllegalArgumentException("Captains cannot remove themselves. Transfer ownership or disband the team.");
        }

        boolean removed = team.getMembers().removeIf(member -> member.getId().equals(memberToRemoveId));

        if (!removed) {
            throw new IllegalArgumentException("User is not on this team.");
        }

        teamRepo.saveTeam(team);

        return mapToTeamDto(team);
    }

    @Transactional
    public TeamDto transferCaptaincy(String teamId, String requestId, String newCaptainId) {

        UserEntity oldCaptain = userRepo.findById(requestId).orElseThrow();
        TeamEntity team = teamRepo.findById(teamId).orElseThrow();

        if (!team.getCaptain().getId().equals(requestId)) {
            throw new SecurityException("Only the captain can pass on the mantle");
        }
        UserEntity newCaptain = userRepo.findById(newCaptainId).orElseThrow();

        boolean isOnRoster = team.getMembers().stream().anyMatch(m -> m.getId().equals(newCaptainId));
        if (!isOnRoster) throw new IllegalArgumentException("New captain must be on the team.");

        team.setCaptain(newCaptain);
        teamRepo.saveTeam(team);
        return mapToTeamDto(team);
    }

    private TeamDto mapToTeamDto(TeamEntity team) {
        UserDto captain = mapToUserDto(team.getCaptain());

        List<UserDto> members = team.getMembers().stream()
                .map(this::mapToUserDto)
                .toList();

        return new TeamDto(
                team.getId(),
                team.getName(),
                team.getAvatar(),
                captain,
                members
        );
    }

    private UserDto mapToUserDto(UserEntity user) {
        if (user == null) return null;
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getAvatar(),
                user.getEmail()
        );
    }
}
