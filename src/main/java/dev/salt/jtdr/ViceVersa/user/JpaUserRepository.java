package dev.salt.jtdr.ViceVersa.user;

import dev.salt.jtdr.ViceVersa.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserEntity, String> {
}
