package dev.salt.jtdr.ViceVersa.domain;

import dev.salt.jtdr.ViceVersa.enums.EntryType;
import dev.salt.jtdr.ViceVersa.enums.TournamentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournaments")
@Getter @Setter @NoArgsConstructor
public class TournamentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String game;
    private String format;

    @Enumerated(EnumType.STRING)
    private TournamentStatus status = TournamentStatus.UPCOMING;

    @Enumerated(EnumType.STRING)
    private EntryType entryType;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchEntity> matches = new ArrayList<>();

}
