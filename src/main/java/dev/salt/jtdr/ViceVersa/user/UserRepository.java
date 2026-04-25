package dev.salt.jtdr.ViceVersa.user;

import dev.salt.jtdr.ViceVersa.domain.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    private final JpaUserRepository repo;

    public UserRepository(JpaUserRepository repo) {
        this.repo = repo;
    }

    public Optional<UserEntity> findById(String id) {
        return repo.findById(id);
    }
}
