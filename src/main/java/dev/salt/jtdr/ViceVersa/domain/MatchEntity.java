package dev.salt.jtdr.ViceVersa.domain;

import dev.salt.jtdr.ViceVersa.enums.EntryType;
import dev.salt.jtdr.ViceVersa.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Getter @Setter @NoArgsConstructor
public class MatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private TournamentEntity tournament;

    @Enumerated(EnumType.STRING)
    private EntryType entryType;

    private Integer round;
    private LocalDateTime matchDate;

    @Enumerated(EnumType.STRING)
    private MatchStatus status = MatchStatus.PENDING;

    @Column(name = "player1_id")
    private String player1Id;

    @Column(name = "player2_id")
    private String player2Id;

    @Column(name = "winner_id")
    private String winnerId;

    @Column(name = "score_p1")
    private Integer scoreP1 = 0;

    @Column(name = "score_p2")
    private Integer scoreP2 = 0;
}
