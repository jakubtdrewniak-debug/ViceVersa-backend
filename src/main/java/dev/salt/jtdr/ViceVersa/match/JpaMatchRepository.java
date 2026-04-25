package dev.salt.jtdr.ViceVersa.match;

import dev.salt.jtdr.ViceVersa.domain.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMatchRepository extends JpaRepository<MatchEntity, String> {
}
