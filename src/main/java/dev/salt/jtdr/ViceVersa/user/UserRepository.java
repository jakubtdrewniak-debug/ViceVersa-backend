package dev.salt.jtdr.ViceVersa.user;

import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JpaUserRepository repo;

    public UserRepository(JpaUserRepository repo) {
        this.repo = repo;
    }
}
