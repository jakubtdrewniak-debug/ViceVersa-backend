package dev.salt.jtdr.ViceVersa.repository.user;

import dev.salt.jtdr.ViceVersa.domain.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JpaUserRepository repo;


    public Optional<UserEntity> findById(String id) {
        return repo.findById(id);
    }
}
