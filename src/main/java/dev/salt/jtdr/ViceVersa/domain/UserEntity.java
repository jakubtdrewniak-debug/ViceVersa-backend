package dev.salt.jtdr.ViceVersa.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter @NoArgsConstructor
public class UserEntity {

    @Id
    private String id;

    private String name;

    @Column(unique = true)
    private String email;

    private String avatar;

    @ManyToMany(mappedBy = "members")
    private List<TeamEntity> teams = new ArrayList<>();


}
