package org.example.auth.repo;

import org.example.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailActivationCode(String activationCode);

    Optional<User> findByPasswordResetCode(String code);

    List<User> findAllByAccountNonLockedFalse(); //blocked

    List<User> findAllByEnabledTrue();  //activated

    List<User> findAllByEnabledFalse();  //not activated

    List<User> findAllByUsernameContains(String name);
}
